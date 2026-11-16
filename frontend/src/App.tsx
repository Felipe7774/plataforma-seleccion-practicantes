import { useCallback, useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { changeStatus, downloadCv, getApplicant, getApplicants, getMetrics, login, registerApplicant } from './api'
import type { Applicant, ApplicantDetail, ApplicantStatus, Metrics } from './api'
import './App.css'

type Screen = 'apply' | 'login' | 'dashboard'
const labels: Record<ApplicantStatus, string> = { PENDING: 'Por revisar', VIABLE: 'Viable', NOT_VIABLE: 'No viable' }
const careers = ['Administración de Empresas', 'Contaduría Pública', 'Economía', 'Ingeniería de Sistemas', 'Ingeniería Industrial', 'Mercadeo', 'Otra']

function App() {
  const [screen, setScreen] = useState<Screen>('apply')
  const [token, setToken] = useState(() => localStorage.getItem('accessToken') ?? '')
  const signOut = () => { localStorage.removeItem('accessToken'); setToken(''); setScreen('apply') }

  if (screen === 'dashboard') return <Dashboard token={token} onLogin={() => setScreen('login')} onSignOut={signOut} />
  return <main className="public-shell">
    <PublicHeader onApply={() => setScreen('apply')} onLogin={() => setScreen('login')} />
    {screen === 'apply' ? <ApplicationForm /> : <Login onBack={() => setScreen('apply')} onLogin={() => { setToken(localStorage.getItem('accessToken') ?? ''); setScreen('dashboard') }} />}
  </main>
}

function Brand() { return <span className="wordmark">talento<span aria-hidden="true" /></span> }

function PublicHeader({ onApply, onLogin }: { onApply: () => void; onLogin: () => void }) {
  return <header className="public-header">
    <button className="brand-button" onClick={onApply} aria-label="Ir a la postulación"><Brand /></button>
    <span className="header-claim">JÓVENES QUE TRANSFORMAN</span>
    <span className="header-program">Programa de prácticas&nbsp; / &nbsp;2026</span>
    <button className="analyst-link" onClick={onLogin}>Iniciar sesión admin</button>
  </header>
}

function ApplicationForm() {
  const [message, setMessage] = useState(''); const [error, setError] = useState(''); const [loading, setLoading] = useState(false); const [filename, setFilename] = useState('')
  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault(); setLoading(true); setError(''); setMessage('')
    try { await registerApplicant(new FormData(event.currentTarget)); setMessage('¡Postulación registrada! El equipo de selección revisará tu perfil.'); event.currentTarget.reset(); setFilename('') }
    catch (cause) { setError(cause instanceof Error ? cause.message : 'No fue posible registrar la postulación.') }
    finally { setLoading(false) }
  }
  return <section className="application-layout">
    <aside className="application-story">
      <span className="yellow-line" /><p className="story-kicker">TU TALENTO TIENE FUTURO</p>
      <h1>Tu próximo<br />gran paso<br />empieza aquí.</h1>
      <p className="story-copy">Trae tus ideas. Construye experiencia.<br />Crece con un equipo que cree en ti.</p>
      <ol className="application-steps">
        <li className="active"><span>01</span><div><strong>Cuéntanos sobre ti</strong><small>Completa tus datos académicos.</small></div></li>
        <li><span>02</span><div><strong>Comparte tu hoja de vida</strong><small>Queremos conocer tu potencial.</small></div></li>
        <li><span>03</span><div><strong>Da el siguiente paso</strong><small>Nuestro equipo revisará tu perfil.</small></div></li>
      </ol>
    </aside>
    <div className="application-content">
      <p className="section-kicker">POSTULACIÓN A PRÁCTICAS</p><h2>Hagamos que suceda</h2><p className="section-lead">Completa el formulario y da el primer paso de tu carrera.</p>
      <form className="application-form" onSubmit={submit}>
        <fieldset><legend>1. Información personal y académica</legend><p>Todos los campos son obligatorios.</p><div className="field-grid">
          <label className="field-wide">Nombre completo *<input required name="fullName" autoComplete="name" placeholder="Como aparece en tu documento" /></label>
          <label>Correo electrónico *<input required type="email" name="email" autoComplete="email" placeholder="tunombre@correo.com" /></label>
          <label>Carrera *<select required name="career" defaultValue=""><option value="" disabled>Selecciona tu carrera</option>{careers.map(career => <option key={career}>{career}</option>)}</select></label>
          <label>Semestre actual *<select required name="semester" defaultValue=""><option value="" disabled>Selecciona tu semestre</option>{Array.from({ length: 12 }, (_, index) => <option value={index + 1} key={index + 1}>{index + 1}.º semestre</option>)}</select></label>
        </div></fieldset>
        <fieldset><legend>2. Hoja de vida</legend><label className="upload-zone">
          <input required type="file" name="cv" accept="application/pdf" aria-label="Hoja de vida (PDF)" onChange={event => setFilename(event.target.files?.[0]?.name ?? '')} />
          <span className="upload-icon" aria-hidden="true">PDF</span><span className="upload-copy"><strong>{filename || 'Selecciona tu hoja de vida'}</strong><small>{filename ? 'Archivo PDF seleccionado' : 'Solo PDF · Tamaño máximo: 5 MB'}</small></span><span className="upload-action">Seleccionar archivo</span>
        </label></fieldset>
        <label className="privacy-check"><input required type="checkbox" /><span>Autorizo el tratamiento de mis datos personales y acepto la <a href="#privacy">política de privacidad.</a></span></label>
        {error && <p className="notice error">{error}</p>}{message && <p className="notice success">{message}</p>}
        <button className="submit-application" disabled={loading}>{loading ? 'Enviando postulación…' : 'Enviar postulación'} <span>→</span></button>
      </form>
    </div>
  </section>
}

function Login({ onLogin, onBack }: { onLogin: () => void; onBack: () => void }) {
  const [error, setError] = useState(''); const [loading, setLoading] = useState(false)
  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault(); setLoading(true); setError(''); const form = new FormData(event.currentTarget)
    try { const session = await login(String(form.get('username')), String(form.get('password'))); localStorage.setItem('accessToken', session.accessToken); onLogin() }
    catch (cause) { setError(cause instanceof Error ? cause.message : 'No fue posible iniciar sesión.') }
    finally { setLoading(false) }
  }
  return <section className="login-page"><div className="login-panel">
    <button className="back-link" onClick={onBack}>← Volver a la postulación</button><span className="yellow-line" /><p className="section-kicker">ACCESO INTERNO</p><h1>Equipo de selección</h1><p>Ingresa con tus credenciales para revisar y evaluar las postulaciones.</p>
    <form onSubmit={submit}><label>Usuario<input name="username" required autoComplete="username" /></label><label>Contraseña<input name="password" type="password" required autoComplete="current-password" /></label>{error && <p className="notice error">{error}</p>}<button className="submit-application" disabled={loading}>{loading ? 'Ingresando…' : 'Iniciar sesión'} <span>→</span></button></form>
  </div></section>
}

function Dashboard({ token, onLogin, onSignOut }: { token: string; onLogin: () => void; onSignOut: () => void }) {
  const [metrics, setMetrics] = useState<Metrics | null>(null); const [applicants, setApplicants] = useState<Applicant[]>([]); const [selected, setSelected] = useState<ApplicantDetail | null>(null); const [query, setQuery] = useState(''); const [status, setStatus] = useState(''); const [sort, setSort] = useState('recent'); const [error, setError] = useState(''); const [loading, setLoading] = useState(true)
  const load = useCallback(async (nextQuery = '', nextStatus = '') => {
    if (!token) { onLogin(); return } await Promise.resolve(); setLoading(true); setError('')
    try { const [nextMetrics, page] = await Promise.all([getMetrics(token), getApplicants(token, { ...(nextQuery ? { q: nextQuery } : {}), ...(nextStatus ? { status: nextStatus } : {}) })]); setMetrics(nextMetrics); setApplicants(page.items) }
    catch (cause) { setError(cause instanceof Error ? cause.message : 'No fue posible cargar el panel.') }
    finally { setLoading(false) }
  }, [onLogin, token])
  useEffect(() => { queueMicrotask(() => { void load() }) }, [load])
  const sortedApplicants = useMemo(() => [...applicants].sort((a, b) => (new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()) * (sort === 'oldest' ? 1 : -1)), [applicants, sort])
  const choose = async (applicant: Applicant) => { try { setSelected(await getApplicant(token, applicant.id)) } catch (cause) { setError(cause instanceof Error ? cause.message : 'No fue posible cargar el detalle.') } }
  const decide = async (nextStatus: ApplicantStatus, comment: string) => { if (!selected) return; try { setSelected(await changeStatus(token, selected.id, nextStatus, comment)); await load(query, status) } catch (cause) { setError(cause instanceof Error ? cause.message : 'No fue posible guardar la decisión.') } }
  const exportCsv = () => {
    const rows = [['Nombre', 'Correo', 'Carrera', 'Semestre', 'Estado', 'Postulación'], ...sortedApplicants.map(item => [item.fullName, item.email, item.career, String(item.semester), labels[item.status], formatDate(item.createdAt)])]
    const csv = rows.map(row => row.map(value => `"${value.replaceAll('"', '""')}"`).join(',')).join('\n'); const url = URL.createObjectURL(new Blob([`\uFEFF${csv}`], { type: 'text/csv;charset=utf-8' })); const link = document.createElement('a'); link.href = url; link.download = 'postulaciones.csv'; link.click(); URL.revokeObjectURL(url)
  }
  return <main className="admin-shell">
    <aside className="admin-sidebar"><Brand /><p>GESTIÓN DE PRÁCTICAS</p><nav aria-label="Panel administrativo"><button onClick={() => setSelected(null)}><span>▦</span> Resumen</button><button className="active" onClick={() => setSelected(null)}><span>♙</span> Practicantes</button></nav><button className="sidebar-logout" onClick={onSignOut}>Salir</button></aside>
    <section className="admin-workspace"><header className="admin-topbar"><span>Talento humano</span><span>Convocatoria 2026-2</span><span className="notification">♧</span><div><strong>Equipo de selección</strong><small>Analista</small></div></header>{error && <p className="notice error admin-notice">{error}</p>}
      {selected ? <Detail applicant={selected} token={token} onBack={() => setSelected(null)} onDecide={decide} /> : <ApplicantsDashboard metrics={metrics} applicants={sortedApplicants} loading={loading} query={query} status={status} sort={sort} onQuery={setQuery} onStatus={setStatus} onSort={setSort} onFilter={() => void load(query, status)} onChoose={choose} onExport={exportCsv} />}
    </section>
  </main>
}

function ApplicantsDashboard({ metrics, applicants, loading, query, status, sort, onQuery, onStatus, onSort, onFilter, onChoose, onExport }: { metrics: Metrics | null; applicants: Applicant[]; loading: boolean; query: string; status: string; sort: string; onQuery: (value: string) => void; onStatus: (value: string) => void; onSort: (value: string) => void; onFilter: () => void; onChoose: (applicant: Applicant) => void; onExport: () => void }) {
  const count = (wanted: ApplicantStatus) => metrics?.byStatus.find(item => item.status === wanted)?.count ?? 0
  return <div className="admin-content">
    <div className="page-title-row"><div><h1>Practicantes</h1><p>Cada perfil es una nueva oportunidad. Encuentra el próximo gran talento.</p></div><button className="export-button" onClick={onExport}>⇩ &nbsp; Exportar listado</button></div>
    <div className="metric-grid"><Metric label="Total de postulaciones" value={metrics?.total ?? '—'} tone="blue" icon="♙" /><Metric label="Por revisar" value={metrics ? count('PENDING') : '—'} tone="yellow" icon="◷" /><Metric label="Viables" value={metrics ? count('VIABLE') : '—'} tone="green" icon="✓" /><Metric label="No viables" value={metrics ? count('NOT_VIABLE') : '—'} tone="red" icon="×" /></div>
    <div className="list-tabs"><button className="active">Todas las postulaciones</button><button onClick={() => { onStatus('PENDING'); queueMicrotask(onFilter) }}>Por revisar</button></div>
    <div className="table-filters"><div className="search-field">⌕<input aria-label="Buscar" placeholder="Buscar por nombre o correo electrónico" value={query} onChange={event => onQuery(event.target.value)} onKeyDown={event => event.key === 'Enter' && onFilter()} /></div><select aria-label="Filtrar por estado" value={status} onChange={event => { onStatus(event.target.value); queueMicrotask(onFilter) }}><option value="">Estado: Todos</option><option value="PENDING">Por revisar</option><option value="VIABLE">Viables</option><option value="NOT_VIABLE">No viables</option></select><select aria-label="Ordenar postulaciones" value={sort} onChange={event => onSort(event.target.value)}><option value="recent">Más recientes</option><option value="oldest">Más antiguos</option></select></div>
    <section className="applicant-table" aria-label="Listado de practicantes"><div className="table-head"><span>Practicante</span><span>Carrera</span><span>Semestre</span><span>Postulación</span><span>Estado</span><span /></div>{loading ? <p className="table-empty">Cargando postulaciones…</p> : applicants.length === 0 ? <p className="table-empty">No encontramos postulaciones con esos filtros.</p> : applicants.map(applicant => <button className="table-row" onClick={() => onChoose(applicant)} key={applicant.id} aria-label={`Ver perfil de ${applicant.fullName}`}><span><strong>{applicant.fullName}</strong><small>{applicant.email}</small></span><span>{applicant.career}</span><span>{applicant.semester}.º</span><span>{formatDate(applicant.createdAt)}</span><span><Status status={applicant.status} /></span><span className="row-arrow">›</span></button>)}</section>
  </div>
}

function Metric({ label, value, tone, icon }: { label: string; value: number | string; tone: string; icon: string }) { return <article className="metric"><span>{label}</span><strong>{value}</strong><i className={`metric-icon ${tone}`}>{icon}</i></article> }
function Status({ status }: { status: ApplicantStatus }) { return <span className={`badge ${status.toLowerCase()}`}>{labels[status]}</span> }

function Detail({ applicant, token, onBack, onDecide }: { applicant: ApplicantDetail; token: string; onBack: () => void; onDecide: (status: ApplicantStatus, comment: string) => Promise<void> }) {
  const [comment, setComment] = useState(''); const [saving, setSaving] = useState(false); const [downloading, setDownloading] = useState(false); const [downloadError, setDownloadError] = useState('')
  const save = async (status: ApplicantStatus) => { if (status === 'NOT_VIABLE' && !comment.trim()) return; setSaving(true); await onDecide(status, comment); setComment(''); setSaving(false) }
  const download = async () => { setDownloading(true); setDownloadError(''); try { const blob = await downloadCv(token, applicant.id); const url = URL.createObjectURL(blob); const link = document.createElement('a'); link.href = url; link.download = applicant.cvOriginalName; document.body.appendChild(link); link.click(); link.remove(); URL.revokeObjectURL(url) } catch (cause) { setDownloadError(cause instanceof Error ? cause.message : 'No fue posible descargar la hoja de vida.') } finally { setDownloading(false) } }
  const initials = applicant.fullName.split(' ').slice(0, 2).join(' ')
  return <div className="admin-content detail-page">
    <button className="back-link" onClick={onBack}>← &nbsp; Volver a practicantes</button><div className="profile-title"><div><h1>{applicant.fullName}</h1><p>Postulación #{applicant.id.slice(0, 6).toUpperCase()} &nbsp;·&nbsp; Recibida el {formatLongDate(applicant.createdAt)}</p></div><Status status={applicant.status} /></div>
    <div className="detail-grid"><div className="detail-main"><section className="detail-card info-card"><h2>Información del practicante</h2><dl><div><dt>Nombre completo</dt><dd>{applicant.fullName}</dd></div><div><dt>Correo electrónico</dt><dd>{applicant.email}</dd></div><div><dt>Carrera</dt><dd>{applicant.career}</dd></div><div><dt>Semestre actual</dt><dd>{applicant.semester}.º semestre</dd></div></dl></section>
      <section className="detail-card cv-card"><div className="card-heading"><h2>Hoja de vida</h2><span>PDF</span></div><div className="cv-preview"><div className="document-sheet"><b>{initials}</b><span /><small>PERFIL</small><i /><i /><small>FORMACIÓN ACADÉMICA</small><i /></div><div className="cv-meta"><h3>{applicant.cvOriginalName}</h3><p>Documento PDF adjunto</p><p>Adjunto el {formatLongDate(applicant.createdAt)}</p><p className="cv-help">Consulta el documento completo antes de tomar una decisión.</p><button aria-label="Descargar CV" disabled={downloading} onClick={() => void download()}>{downloading ? 'Abriendo…' : '↗ &nbsp; Abrir documento'}</button>{downloadError && <p className="notice error">{downloadError}</p>}</div></div></section></div>
      <aside className="detail-aside"><section className="detail-card evaluation-card"><span className="yellow-line" /><h2>Evaluación del perfil</h2><p>Revisa la información y define si el perfil continúa en el proceso de selección.</p><label>Observaciones <small>Opcional</small><textarea value={comment} onChange={event => setComment(event.target.value)} maxLength={1000} placeholder="Escribe los motivos de tu decisión…" /></label><div className="decision-buttons"><button className="viable-button" disabled={saving} onClick={() => void save('VIABLE')}>✓ &nbsp; Viable</button><button className="reject-button" disabled={saving || !comment.trim()} onClick={() => void save('NOT_VIABLE')}>× &nbsp; No viable</button></div><small>Al elegir una opción se guardará el estado. Podrás actualizarlo desde este detalle.</small></section>
        <section className="detail-card activity-card"><h2>Actividad</h2><div className="activity-item"><span>▧</span><div><strong>Postulación recibida</strong><small>{formatLongDate(applicant.createdAt)}</small></div></div>{applicant.history.map(item => <div className="activity-item" key={item.id}><span>✓</span><div><strong>{labels[item.newStatus]}</strong><p>{item.comment || 'Sin observaciones'}</p><small>{item.changedBy} · {formatLongDate(item.changedAt)}</small></div></div>)}</section></aside>
    </div>
  </div>
}

function formatDate(value: string) { return new Intl.DateTimeFormat('es-CO', { day: '2-digit', month: 'short', year: 'numeric' }).format(new Date(value)).replace('.', '') }
function formatLongDate(value: string) { return new Intl.DateTimeFormat('es-CO', { day: 'numeric', month: 'long', year: 'numeric' }).format(new Date(value)) }

export default App
