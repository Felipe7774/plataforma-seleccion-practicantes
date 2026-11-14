import { useCallback, useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import {
  changeStatus,
  getApplicant,
  getApplicants,
  getMetrics,
  login,
  registerApplicant,
} from './api'
import type { Applicant, ApplicantDetail, ApplicantStatus, Metrics } from './api'
import './App.css'

type Screen = 'home' | 'apply' | 'login' | 'dashboard'
const labels: Record<ApplicantStatus, string> = { PENDING: 'Pendiente', VIABLE: 'Viable', NOT_VIABLE: 'No viable' }

function App() {
  const [screen, setScreen] = useState<Screen>('home')
  const [token, setToken] = useState(() => localStorage.getItem('accessToken') ?? '')

  const signOut = () => {
    localStorage.removeItem('accessToken')
    setToken('')
    setScreen('home')
  }

  return (
    <main className="page-shell">
      <nav className="topbar" aria-label="Navegación principal">
        <button className="brand" onClick={() => setScreen('home')} aria-label="Inicio">
          <span className="brand-mark">P</span><span>Práctica</span>
        </button>
        <div className="nav-actions">
          <button className="text-button" onClick={() => setScreen('apply')}>Postularme</button>
          {token ? <><button className="text-button" onClick={() => setScreen('dashboard')}>Selección</button><button className="text-button" onClick={signOut}>Salir</button></> :
            <button className="text-button" onClick={() => setScreen('login')}>Equipo de selección</button>}
        </div>
      </nav>
      {screen === 'home' && <Home go={setScreen} />}
      {screen === 'apply' && <ApplicationForm onDone={() => setScreen('home')} />}
      {screen === 'login' && <Login onLogin={() => { setToken(localStorage.getItem('accessToken') ?? ''); setScreen('dashboard') }} />}
      {screen === 'dashboard' && <Dashboard token={token} onLogin={() => setScreen('login')} />}
    </main>
  )
}

function Home({ go }: { go: (screen: Screen) => void }) {
  return <>
    <section className="hero"><p className="eyebrow">Tu siguiente oportunidad</p><h1>Impulsa tu camino profesional.</h1><p className="hero-copy">Postúlate de forma simple y permite que el equipo de selección encuentre tu talento.</p><button className="primary" onClick={() => go('apply')}>Iniciar postulación <span>→</span></button></section>
    <section className="feature-grid" aria-label="Etapas del proceso">
      <article><span>01</span><h2>Registra tu perfil</h2><p>Completa tus datos académicos y adjunta tu hoja de vida en PDF.</p></article>
      <article><span>02</span><h2>Revisión clara</h2><p>El equipo revisa cada postulación con un proceso trazable.</p></article>
      <article><span>03</span><h2>Seguimiento</h2><p>Recibirás una notificación cuando cambie el estado de tu proceso.</p></article>
    </section>
  </>
}

function ApplicationForm({ onDone }: { onDone: () => void }) {
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault(); setLoading(true); setError(''); setMessage('')
    try { await registerApplicant(new FormData(event.currentTarget)); setMessage('¡Postulación registrada! Recibirás novedades en tu correo.'); event.currentTarget.reset() }
    catch (cause) { setError(cause instanceof Error ? cause.message : 'No fue posible registrar la postulación.') }
    finally { setLoading(false) }
  }
  return <section className="content narrow"><button className="back" onClick={onDone}>← Volver</button><p className="eyebrow">Postulación</p><h1>Cuéntanos sobre ti.</h1><p className="intro">Adjunta tu hoja de vida en PDF. El archivo puede pesar máximo 5 MB.</p>
    <form className="form-card" onSubmit={submit}>
      <label>Nombre completo<input required name="fullName" autoComplete="name" /></label>
      <label>Correo electrónico<input required type="email" name="email" autoComplete="email" /></label>
      <div className="form-row"><label>Carrera<input required name="career" /></label><label>Semestre<input required type="number" name="semester" min="1" max="12" /></label></div>
      <label>Hoja de vida (PDF)<input required type="file" name="cv" accept="application/pdf" /></label>
      {error && <p className="notice error">{error}</p>}{message && <p className="notice success">{message}</p>}
      <button className="primary" disabled={loading}>{loading ? 'Enviando…' : 'Enviar postulación'}</button>
    </form>
  </section>
}

function Login({ onLogin }: { onLogin: () => void }) {
  const [error, setError] = useState(''); const [loading, setLoading] = useState(false)
  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault(); setLoading(true); setError(''); const form = new FormData(event.currentTarget)
    try { const session = await login(String(form.get('username')), String(form.get('password'))); localStorage.setItem('accessToken', session.accessToken); onLogin() }
    catch (cause) { setError(cause instanceof Error ? cause.message : 'No fue posible iniciar sesión.') }
    finally { setLoading(false) }
  }
  return <section className="content narrow"><p className="eyebrow">Acceso interno</p><h1>Equipo de selección</h1><p className="intro">Ingresa con tus credenciales para revisar las postulaciones.</p><form className="form-card" onSubmit={submit}><label>Usuario<input name="username" required autoComplete="username" /></label><label>Contraseña<input name="password" type="password" required autoComplete="current-password" /></label>{error && <p className="notice error">{error}</p>}<button className="primary" disabled={loading}>{loading ? 'Ingresando…' : 'Iniciar sesión'}</button></form></section>
}

function Dashboard({ token, onLogin }: { token: string; onLogin: () => void }) {
  const [metrics, setMetrics] = useState<Metrics | null>(null); const [applicants, setApplicants] = useState<Applicant[]>([]); const [selected, setSelected] = useState<ApplicantDetail | null>(null); const [query, setQuery] = useState(''); const [status, setStatus] = useState(''); const [error, setError] = useState(''); const [loading, setLoading] = useState(true)
  const load = useCallback(async (nextQuery = '', nextStatus = '') => {
    if (!token) { onLogin(); return }
    await Promise.resolve()
    setLoading(true); setError('')
    try { const [nextMetrics, page] = await Promise.all([getMetrics(token), getApplicants(token, { ...(nextQuery ? { q: nextQuery } : {}), ...(nextStatus ? { status: nextStatus } : {}) })]); setMetrics(nextMetrics); setApplicants(page.items) }
    catch (cause) { setError(cause instanceof Error ? cause.message : 'No fue posible cargar el panel.') }
    finally { setLoading(false) }
  }, [onLogin, token])
  useEffect(() => { void load() }, [load])
  const choose = async (applicant: Applicant) => { try { setSelected(await getApplicant(token, applicant.id)) } catch (cause) { setError(cause instanceof Error ? cause.message : 'No fue posible cargar el detalle.') } }
  const decide = async (nextStatus: ApplicantStatus, comment: string) => { if (!selected) return; try { setSelected(await changeStatus(token, selected.id, nextStatus, comment)); await load(query, status) } catch (cause) { setError(cause instanceof Error ? cause.message : 'No fue posible guardar la decisión.') } }
  return <section className="dashboard"><div className="dashboard-title"><div><p className="eyebrow">Panel interno</p><h1>Selección de practicantes</h1></div><button className="secondary" onClick={() => void load(query, status)}>Actualizar</button></div>{error && <p className="notice error">{error}</p>}
    <div className="metric-grid"><Metric label="Postulaciones" value={metrics?.total ?? '—'} /><Metric label="Pendientes" value={metrics?.byStatus.find(item => item.status === 'PENDING')?.count ?? '—'} /><Metric label="Viables" value={metrics?.byStatus.find(item => item.status === 'VIABLE')?.count ?? '—'} /></div>
    <div className="workspace"><section className="list-panel"><div className="filters"><input aria-label="Buscar" placeholder="Buscar por nombre o correo" value={query} onChange={event => setQuery(event.target.value)} onKeyDown={event => event.key === 'Enter' && void load(query, status)} /><select aria-label="Filtrar por estado" value={status} onChange={event => setStatus(event.target.value)}><option value="">Todos los estados</option><option value="PENDING">Pendientes</option><option value="VIABLE">Viables</option><option value="NOT_VIABLE">No viables</option></select><button className="secondary" onClick={() => void load(query, status)}>Filtrar</button></div>{loading ? <p className="empty">Cargando postulaciones…</p> : applicants.length === 0 ? <p className="empty">No encontramos postulaciones con esos filtros.</p> : <div className="applicant-list">{applicants.map(applicant => <button className={`applicant ${selected?.id === applicant.id ? 'selected' : ''}`} onClick={() => void choose(applicant)} key={applicant.id}><span><strong>{applicant.fullName}</strong><small>{applicant.career} · Semestre {applicant.semester}</small></span><Status status={applicant.status} /></button>)}</div>}</section><Detail applicant={selected} onDecide={decide} /></div>
  </section>
}

function Metric({ label, value }: { label: string; value: number | string }) { return <article className="metric"><span>{label}</span><strong>{value}</strong></article> }
function Status({ status }: { status: ApplicantStatus }) { return <span className={`badge ${status.toLowerCase()}`}>{labels[status]}</span> }
function Detail({ applicant, onDecide }: { applicant: ApplicantDetail | null; onDecide: (status: ApplicantStatus, comment: string) => Promise<void> }) {
  const [comment, setComment] = useState(''); const [saving, setSaving] = useState(false)
  if (!applicant) return <aside className="detail empty"><p>Selecciona una postulación para consultar sus datos e historial.</p></aside>
  const save = async (status: ApplicantStatus) => { if (status === 'NOT_VIABLE' && !comment.trim()) return; setSaving(true); await onDecide(status, comment); setComment(''); setSaving(false) }
  return <aside className="detail"><div className="detail-head"><div><p className="eyebrow">Postulante</p><h2>{applicant.fullName}</h2><p>{applicant.email}</p></div><Status status={applicant.status} /></div><dl><div><dt>Carrera</dt><dd>{applicant.career}</dd></div><div><dt>Semestre</dt><dd>{applicant.semester}</dd></div><div><dt>Hoja de vida</dt><dd>{applicant.cvOriginalName}</dd></div></dl><div className="decision"><h3>Registrar decisión</h3><textarea value={comment} onChange={event => setComment(event.target.value)} maxLength={1000} placeholder="Comentario (obligatorio para no viable)" /><div><button className="secondary" disabled={saving} onClick={() => void save('VIABLE')}>Marcar viable</button><button className="danger" disabled={saving || !comment.trim()} onClick={() => void save('NOT_VIABLE')}>No viable</button></div></div><div className="history"><h3>Historial</h3>{applicant.history.length === 0 ? <p>Sin decisiones registradas.</p> : applicant.history.map(item => <article key={item.id}><Status status={item.newStatus} /><p>{item.comment || 'Sin comentario'}</p><small>{item.changedBy} · {new Date(item.changedAt).toLocaleString('es-CO')}</small></article>)}</div></aside>
}

export default App

