import request from './request.js'

export const getKnowledgeNodes = (params) => request.get('/knowledge/nodes', { params })

export const getKnowledgeNode = (id) => request.get(`/knowledge/nodes/${id}`)

export const createKnowledgeNode = (data) => request.post('/knowledge/nodes', data)

export const updateKnowledgeNode = (id, data) => request.put(`/knowledge/nodes/${id}`, data)

export const deleteKnowledgeNode = (id) => request.delete(`/knowledge/nodes/${id}`)

export const getKnowledgeGraph = () => request.get('/knowledge/graph')

export const getTodayKnowledgeNodes = () => request.get('/knowledge/nodes/today')

export const reviewKnowledgeNode = (id, result) => request.post(`/knowledge/nodes/${id}/review`, { result })

export const getRecommendNodes = () => request.get('/knowledge/recommend')

export const getKnowledgeStats = () => request.get('/knowledge/stats')
