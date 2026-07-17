import request from './request.js'

export const getTasks = (params) => request.get('/tasks', { params })

export const getTask = (id) => request.get(`/tasks/${id}`)

export const createTask = (data) => request.post('/tasks', data)

export const updateTask = (id, data) => request.put(`/tasks/${id}`, data)

export const deleteTask = (id) => request.delete(`/tasks/${id}`)

export const updateTaskStatus = (id, status) => request.put(`/tasks/${id}/status?status=${status}`)

export const getDayStats = (date) => request.get('/tasks/stats', { params: { date } })
