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
    downloadCv: vi.fn(),
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
    localStorage.clear()
  })

  it('shows the public application form on the main page', () => {
    render(<App />)

    expect(screen.getByRole('heading', { name: /hagamos que suceda/i })).toBeTruthy()
    expect(screen.getByLabelText(/hoja de vida/i)).toBeTruthy()
    expect(screen.getByRole('button', { name: /iniciar sesión admin/i })).toBeTruthy()
    expect(screen.queryByText(/arrastra/i)).toBeNull()
  })

  it('authenticates a selection user and opens the dashboard', async () => {
    const user = userEvent.setup()
    vi.mocked(api.login).mockResolvedValue({ accessToken: 'jwt-for-test' })
    vi.mocked(api.getMetrics).mockResolvedValue({ total: 1, byStatus: [], byCareer: [] })
    vi.mocked(api.getApplicants).mockResolvedValue({ items: [], totalElements: 0, totalPages: 0, page: 0, size: 10 })
    render(<App />)

    await user.click(screen.getByRole('button', { name: /iniciar sesión admin/i }))
    await user.type(screen.getByLabelText('Usuario'), 'analista')
    await user.type(screen.getByLabelText('Contraseña'), 'secreto')
    await user.click(screen.getByRole('button', { name: /^iniciar sesión →$/i }))

    await waitFor(() => expect(screen.getByRole('heading', { name: /practicantes/i })).toBeTruthy())
    expect(localStorage.getItem('accessToken')).toBe('jwt-for-test')
    expect(screen.queryByRole('button', { name: /resumen/i })).toBeNull()
  })

  it('applies every status filter, restores all records and searches by name', async () => {
    const user = userEvent.setup()
    vi.mocked(api.login).mockResolvedValue({ accessToken: 'jwt-for-test' })
    vi.mocked(api.getMetrics).mockResolvedValue({ total: 3, byStatus: [], byCareer: [] })
    vi.mocked(api.getApplicants).mockResolvedValue({ items: [], totalElements: 0, totalPages: 0, page: 0, size: 10 })
    render(<App />)

    await user.click(screen.getByRole('button', { name: /iniciar sesión admin/i }))
    await user.type(screen.getByLabelText('Usuario'), 'analista')
    await user.type(screen.getByLabelText('Contraseña'), 'secreto')
    await user.click(screen.getByRole('button', { name: /^iniciar sesión →$/i }))
    await screen.findByRole('heading', { name: /practicantes/i })

    const statusFilter = screen.getByLabelText('Filtrar por estado')
    await user.selectOptions(statusFilter, 'NOT_VIABLE')
    await waitFor(() => expect(api.getApplicants).toHaveBeenLastCalledWith('jwt-for-test', { status: 'NOT_VIABLE' }))

    await user.selectOptions(statusFilter, 'VIABLE')
    await waitFor(() => expect(api.getApplicants).toHaveBeenLastCalledWith('jwt-for-test', { status: 'VIABLE' }))

    await user.selectOptions(statusFilter, 'PENDING')
    await waitFor(() => expect(api.getApplicants).toHaveBeenLastCalledWith('jwt-for-test', { status: 'PENDING' }))

    await user.selectOptions(statusFilter, 'VIABLE')
    await user.type(screen.getByLabelText('Buscar'), 'Laura')
    await waitFor(() => expect(api.getApplicants).toHaveBeenLastCalledWith('jwt-for-test', { q: 'Laura', status: 'VIABLE' }))

    await user.click(screen.getByRole('button', { name: /todas las postulaciones/i }))
    await waitFor(() => expect(api.getApplicants).toHaveBeenLastCalledWith('jwt-for-test', { q: 'Laura' }))
  })

  it('loads a candidate detail and records a non-viable decision with a comment', async () => {
    const user = userEvent.setup()
    localStorage.setItem('accessToken', 'stored-token')
    vi.mocked(api.login).mockResolvedValue({ accessToken: 'stored-token' })
    vi.mocked(api.getMetrics).mockResolvedValue({ total: 1, byStatus: [{ status: 'PENDING', count: 1 }], byCareer: [] })
    vi.mocked(api.getApplicants).mockResolvedValue({ items: [applicant], totalElements: 1, totalPages: 1, page: 0, size: 10 })
    vi.mocked(api.getApplicant).mockResolvedValue(applicantDetail)
    vi.mocked(api.changeStatus).mockResolvedValue({
      ...applicantDetail,
      status: 'NOT_VIABLE',
      history: [{ id: 'history-1', previousStatus: 'PENDING', newStatus: 'NOT_VIABLE', comment: 'Semestre no compatible', changedBy: 'analista', changedAt: '2026-11-14T12:30:00Z' }],
    })
    render(<App />)

    await user.click(screen.getByRole('button', { name: /iniciar sesión admin/i }))
    await user.type(screen.getByLabelText('Usuario'), 'analista')
    await user.type(screen.getByLabelText('Contraseña'), 'secreto')
    await user.click(screen.getByRole('button', { name: /^iniciar sesión →$/i }))
    await user.click(await screen.findByRole('button', { name: /ver perfil de laura gómez/i }))
    await screen.findByText('laura-gomez-cv.pdf')
    expect(screen.getByRole('button', { name: /descargar cv/i })).toBeTruthy()
    await user.type(screen.getByPlaceholderText(/motivos de tu decisión/i), 'Semestre no compatible')
    await user.click(screen.getByRole('button', { name: /no viable/i }))

    await waitFor(() => expect(api.changeStatus).toHaveBeenCalledWith('stored-token', applicant.id, 'NOT_VIABLE', 'Semestre no compatible'))
    expect(screen.getByText('Semestre no compatible')).toBeTruthy()
  })

  it('shows the CV download action in the applicant detail', async () => {
    const user = userEvent.setup()
    localStorage.setItem('accessToken', 'stored-token')
    vi.mocked(api.login).mockResolvedValue({ accessToken: 'stored-token' })
    vi.mocked(api.getMetrics).mockResolvedValue({ total: 1, byStatus: [], byCareer: [] })
    vi.mocked(api.getApplicants).mockResolvedValue({ items: [applicant], totalElements: 1, totalPages: 1, page: 0, size: 10 })
    vi.mocked(api.getApplicant).mockResolvedValue(applicantDetail)
    render(<App />)

    await user.click(screen.getByRole('button', { name: /iniciar sesión admin/i }))
    await user.type(screen.getByLabelText('Usuario'), 'analista')
    await user.type(screen.getByLabelText('Contraseña'), 'secreto')
    await user.click(screen.getByRole('button', { name: /^iniciar sesión →$/i }))
    await user.click(await screen.findByRole('button', { name: /ver perfil de laura gómez/i }))

    expect((await screen.findByRole('button', { name: /descargar cv/i })).textContent).toBe('Abrir documento')
    expect(screen.queryByText(/&nbsp;/i)).toBeNull()
  })
})
