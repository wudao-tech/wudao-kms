/**
 * 数据处理相关的工具函数
 */

/**
 * 判断值是否为空值
 * @param {any} value 要判断的值
 * @returns {boolean} 如果是空值返回true，否则返回false
 */
export const isEmpty = (value: any): boolean => {
  return (
    value === null || 
    value === undefined || 
    value === '' || 
    (Array.isArray(value) && value.length === 0)
  )
}

/**
 * 判断值是否为非空值
 * @param {any} value 要判断的值
 * @returns {boolean} 如果是非空值返回true，否则返回false
 */
export const isNotEmpty = (value: any): boolean => {
  return !isEmpty(value)
}

/**
 * 过滤对象中的空值
 * @param {Object} obj 要过滤的对象
 * @returns {Object} 过滤后的对象
 */
export const filterEmptyValues = (obj: Record<string, any>): Record<string, any> => {
  const filtered: Record<string, any> = {}
  
  Object.keys(obj).forEach(key => {
    const value = obj[key]
    if (isNotEmpty(value)) {
      filtered[key] = value
    }
  })
  
  return filtered
}

/**
 * 深度过滤对象中的空值（包括嵌套对象）
 * @param {Object} obj 要过滤的对象
 * @returns {Object} 过滤后的对象
 */
export const deepFilterEmptyValues = (obj: Record<string, any>): Record<string, any> => {
  const filtered: Record<string, any> = {}
  
  Object.keys(obj).forEach(key => {
    const value = obj[key]
    
    if (isNotEmpty(value)) {
      // 如果是普通对象，递归处理
      if (typeof value === 'object' && !Array.isArray(value) && value !== null) {
        const nestedFiltered = deepFilterEmptyValues(value)
        // 只有过滤后的对象不为空时才添加
        if (Object.keys(nestedFiltered).length > 0) {
          filtered[key] = nestedFiltered
        }
      } else {
        filtered[key] = value
      }
    }
  })
  
  return filtered
}

/**
 * 移除数组中的空值元素
 * @param {Array} arr 要过滤的数组
 * @returns {Array} 过滤后的数组
 */
export const filterEmptyArrayItems = (arr: any[]): any[] => {
  return arr.filter(item => isNotEmpty(item))
} 