import request from './request.js'

export const getMistakes = (params) => request.get('/mistakes', { params })

export const getMistake = (id) => request.get(`/mistakes/${id}`)

export const createMistake = (data) => request.post('/mistakes', data)

export const updateMistake = (id, data) => request.put(`/mistakes/${id}`, data)

export const deleteMistake = (id) => request.delete(`/mistakes/${id}`)

export const getTodayMistakes = () => request.get('/mistakes/today')

export const reviewMistake = (id, result) => request.post(`/mistakes/${id}/review`, { result })

export const getMistakeStats = () => request.get('/mistakes/stats')

export const getWeakPoints = (limit = 10) => request.get('/mistakes/weak-points', { params: { limit } })
