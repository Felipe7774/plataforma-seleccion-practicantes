export type ApplicantStatus = 'PENDING' | 'VIABLE' | 'NOT_VIABLE'

export type Applicant = {
  id: string
  fullName: string
  email: string
  career: string
  semester: number
  status: ApplicantStatus
  createdAt: string
}

export type ApplicantDetail = Applicant & {
  cvOriginalName: string
  updatedAt: string
  history: Array<{
    id: string
    previousStatus: ApplicantStatus
    newStatus: ApplicantStatus
    comment: string | null
    changedBy: string
    changedAt: string
  }>
}

export type Metrics = {
  total: number
  byStatus: Array<{ status: ApplicantStatus; count: number }>
  byCareer: Array<{ career: string; count: number }>
}

export type ApplicantPage = {
  items: Applicant[]
  totalElements: number
  totalPages: number
  page: number
  size: number
}

const baseUrl = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

async function request<T>(path: string, options: RequestInit = {}, token?: string): Promise<T> {
  const response = await fetch(`${baseUrl}${path}`, {
    ...options,
    headers: {
      ...(options.headers ?? {}),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
  })

  if (!response.ok) {
    const problem = await response.json().catch(() => null)
    throw new Error(problem?.detail ?? 'No fue posible completar la solicitud.')
  }

  return response.status === 204 ? (undefined as T) : response.json() as Promise<T>
}

export function registerApplicant(data: FormData) {
  return request<Applicant>('/api/v1/applicants', { method: 'POST', body: data })
}

export function login(username: string, password: string) {
  return request<{ accessToken: string }>('/api/v1/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  })
}

export function getApplicants(token: string, filters: Record<string, string>) {
  const query = new URLSearchParams({ page: '0', size: '10', ...filters })
  return request<ApplicantPage>(`/api/v1/applicants?${query}`, {}, token)
}

export function getApplicant(token: string, id: string) {
  return request<ApplicantDetail>(`/api/v1/applicants/${id}`, {}, token)
}

export function getMetrics(token: string) {
  return request<Metrics>('/api/v1/dashboard/metrics', {}, token)
}

export function changeStatus(token: string, id: string, status: ApplicantStatus, comment: string) {
  return request<ApplicantDetail>(`/api/v1/applicants/${id}/status`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ status, comment }),
  }, token)
}
