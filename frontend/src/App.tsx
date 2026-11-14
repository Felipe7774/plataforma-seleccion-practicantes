import './App.css'

function App() {
  return (
    <main className="page-shell">
      <nav className="topbar" aria-label="Navegación principal">
        <a className="brand" href="/" aria-label="Inicio">
          <span className="brand-mark">P</span>
          <span>Práctica</span>
        </a>
        <span className="status">Proyecto en preparación</span>
      </nav>

      <section className="hero">
        <p className="eyebrow">Tu primer paso profesional</p>
        <h1>Conectamos talento con oportunidades.</h1>
        <p className="hero-copy">
          Plataforma de registro y seguimiento para el proceso de selección de
          practicantes.
        </p>
        <div className="actions">
          <button type="button" disabled>
            Iniciar postulación
          </button>
          <span>El formulario estará disponible en la siguiente fase.</span>
        </div>
      </section>

      <section className="feature-grid" aria-label="Etapas del proceso">
        <article>
          <span>01</span>
          <h2>Registra tu perfil</h2>
          <p>Completa tus datos académicos y adjunta tu hoja de vida.</p>
        </article>
        <article>
          <span>02</span>
          <h2>Revisión clara</h2>
          <p>El equipo de selección evalúa cada postulación.</p>
        </article>
        <article>
          <span>03</span>
          <h2>Seguimiento</h2>
          <p>Recibe una notificación cuando cambie el estado del proceso.</p>
        </article>
      </section>
    </main>
  )
}

export default App

