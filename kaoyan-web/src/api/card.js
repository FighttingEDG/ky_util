import request from './request.js'

export const getSubjects = () => request.get('/subjects')

export const getCards = (params) => request.get('/cards', { params })

export const getCard = (id) => request.get(`/cards/${id}`)

export const createCard = (data) => request.post('/cards', data)

export const updateCard = (id, data) => request.put(`/cards/${id}`, data)

export const deleteCard = (id) => request.delete(`/cards/${id}`)

export const getTodayCards = () => request.get('/cards/today')

export const reviewCard = (id, result) => request.post(`/cards/${id}/review`, { result })

export const getStats = () => request.get('/cards/stats')
