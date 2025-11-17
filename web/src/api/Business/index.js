import request from '@/utils/request'

export const ApiGroup = (params = {}) => {
  const { method = 'get', ...rest } = params
  return request({
    method,
    url: '/api/group',
    ...rest
  })
}

export const ApiEquip = (params = {}) => {
  const { method = 'get', ...rest } = params
  return request({
    method,
    url: '/api/equipment',
    ...rest
  })
}

export const ApiEquipType = (params = {}) => {
  const { method = 'get', ...rest } = params
  return request({
    method,
    url: '/api/equipment_type',
    ...rest
  })
}



export const ApiEquipTypeDtail = (params = {}) => {
  return request({
    method: 'get',
    url: '/api/equipment_type/detail',
    params
  })
}

// /api/equipment_type/thing_model/release
export const ApiEquipTypeRelease = (data = {}) => {
  return request({
    method: 'post',
    url: '/api/equipment_type/thing_model/release',
    data
  })
}
