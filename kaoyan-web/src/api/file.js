import request from './request.js'

export const getFileTree = () => request.get('/files/tree')

export const listFiles = (prefix = '') => request.get('/files/list', { params: { prefix } })

export const listDirs = (prefix = '') => request.get('/files/dirs', { params: { prefix } })

export const uploadFile = (file, prefix = '') => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('prefix', prefix)
  return request.post('/files/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const mkdir = (prefix) => request.post('/files/mkdir', { prefix })

export const deleteFile = (objectName) => request.delete('/files/delete', { params: { objectName } })
