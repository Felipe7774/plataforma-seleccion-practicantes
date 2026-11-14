import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import App from './App'
import * as api from './api'

vi.mock('./api', async () => {
  const actual = await vi.importActual<typeof import('./api')>('./api')
  return {
    ...actual,
    registerApplicant: vi.fn(),
    login: vi.fn(),
    getMetrics: vi.fn(),
    getApplicants: vi.fn(),
    getApplicant: vi.fn(),
    changeStatus: vi.fn(),
  }
})

const applicant = {
  id: 'de34b96d-7e91-4e20-a529-3df90e79ce78',
  fullName: 'Laura Gómez',
  email: 'laura@example.com',
  career: 'Ingeniería de Sistemas',
  semester: 8,
  status: 'PENDING' as const,
  createdAt: '2026-11-14T12:00:00Z',
}

const applicantDetail = {
  ...applicant,
  cvOriginalName: 'laura-gomez-cv.pdf',
  updatedAt: '2026-11-14T12:00:00Z',
  history: [],
}

describe('App', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('shows the public application form from the landing page', async () => {
    const user = userEvent.setup()
    render(<App />)

    await user.click(screen.getByRole('button', { name: /iniciar postulación/i }))

    expect(screen.getByRole('heading', { name: /cuéntanos sobre ti/i })).toBeTruthy()
    expect(screen.getByLabelText(/hoja de vida/i)).toBeTruthy()
  })

  it('authenticates a selection user and opens the dashboard', async () => {
    const user = userEvent.setup()
    vi.mocked(api.login).mockResolvedValue({ accessToken: 'jwt-for-test' })
    vi.mocked(api.getMetrics).mockResolvedValue({ total: 1, byStatus: [], byCareer: [] })
    vi.mocked(api.getApplicants).mockResolvedValue({ items: [], totalElements: 0, totalPages: 0, page: 0, size: 10 })
    render(<App />)

    await user.click(screen.getByRole('button', { name: /equipo de selección/i }))
    await user.type(screen.getByLabelText('Usuario'), 'analista')
    await user.type(screen.getByLabelText('Contraseña'), 'secreto')
    await user.click(screen.getByRole('button', { name: /iniciar sesión/i }))

    await waitFor(() => expect(screen.getByRole('heading', { name: /selección de practicantes/i })).toBeTruthy())
    expect(localStorage.getItem('accessToken')).toBe('jwt-for-test')
  })

  it('loads a candidate detail and records a non-viable decision with a comment', async () => {
    const user = userEvent.setup()
    localStorage.setItem('accessToken', 'stored-token')
    vi.mocked(api.getMetrics).mockResolvedValue({ total: 1, byStatus: [{ status: 'PENDING', count: 1 }], byCareer: [] })
    vi.mocked(api.getApplicants).mockResolvedValue({ items: [applicant], totalElements: 1, totalPages: 1, page: 0, size: 10 })
    vi.mocked(api.getApplicant).mockResolvedValue(applicantDetail)
    vi.mocked(api.changeStatus).mockResolvedValue({
      ...applicantDetail,
      status: 'NOT_VIABLE',
      history: [{ id: 'history-1', previousStatus: 'PENDING', newStatus: 'NOT_VIABLE', comment: 'Semestre no compatible', changedBy: 'analista', changedAt: '2026-11-14T12:30:00Z' }],
    })
    render(<App />)

    await user.click(screen.getByRole('button', { name: 'Selección' }))
    await user.click(await screen.findByRole('button', { name: /laura gómez/i }))
    await screen.findByText('laura-gomez-cv.pdf')
    await user.type(screen.getByPlaceholderText(/comentario/i), 'Semestre no compatible')
    await user.click(screen.getByRole('button', { name: /no viable/i }))

    await waitFor(() => expect(api.changeStatus).toHaveBeenCalledWith('stored-token', applicant.id, 'NOT_VIABLE', 'Semestre no compatible'))
    expect(screen.getByText('Semestre no compatible')).toBeTruthy()
  })
})
