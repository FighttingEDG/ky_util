import request from './request.js'

export const startFocus = (data) => request.post('/focus/start', data)

export const stopFocus = (id) => request.post(`/focus/${id}/stop`)

export const getFocusSessions = (params) => request.get('/focus/sessions', { params })

export const getFocusStats = () => request.get('/focus/stats')
