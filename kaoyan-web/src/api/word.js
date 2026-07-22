import request from './request.js'

export const getWords = (params) => request.get('/words', { params })

export const getWord = (id) => request.get(`/words/${id}`)

export const getStudyQueue = () => request.get('/words/study')

export const reviewWord = (id, result) => request.post(`/words/${id}/review`, { result })

export const getWordStats = () => request.get('/words/stats')

export const getWordWeekly = () => request.get('/words/weekly')

export const getImportStatus = () => request.get('/words/import-status')

export const reimportWords = () => request.post('/words/reimport')
