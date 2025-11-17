export const GROUP_LEVEL = new Map([
  ['groupId', '当前层级'],
  ['rootGroupId', '所有层级']
])

export const NETWORK_STATUS = new Map([
  [true, '在线'],
  [false, '离线']
])

export const RUNNING_STATUS = new Map([
  [true, '告警'],
  [false, '正常']
])

export const ACCESS_TYPE = new Map([
  [1, '直连设备'],
  [2, '网关'],
  [3, '网关子设备']
])

export const ACCESS_MODE = new Map([
  [1, '主动'],
  [2, '被动'],
])

export const BOOL_STATUS = new Map([
  [false, '否'],
  [true, '是']
])

export const ENABLE_STATUS = new Map([
  [false, '未启用'],
  [true, '启用']
])

export const RELEASE_STATUS = new Map([
  [false, '未发布'],
  [true, '已发布']
])

export const COMMUNICATION_MODEL = ['wifi', 'Lora', 'Zigbee', 'NB-IOT']

export const COMMUNICATION_PROTOCOL = ['mqtt', 'tcp', 'modbus', 'http']


// STATIC,LABEL,COLLECT,COMPUTE
export const PROP_TYPE = new Map([
  ['STATIC', '静态属性'],
  ['LABEL', '标签属性'],
  ['COLLECT', '采集属性'],
  ['COMPUTE', '计算属性'],
])

export const PROP_WRITEABLE = new Map([
  ['RW', '读写'],
  ['R', '只读'],
])

export const EVENT_TYPE = new Map([
  ['INFO', '消息'],
  ['ALERT', '告警'],
  ['ERROR', '报错'],
])

export const SERVICE_TYPE = new Map([
  ['SEND_COMMAND', '发送命令'],
  ['SET_ATTRIBUTE', '设置属性'],
])

export const PROP_TAB_TYPE = new Map([
  ['properties', {
    label: '属性',
    key: 'type',
    type: PROP_TYPE
  }],
  ['events', {
    label: '事件',
    key: 'eventType',
    type: EVENT_TYPE
  }],
  ['services', {
    label: '服务',
    key: 'serviceType',
    type: SERVICE_TYPE
  }],
])

// int,float,double,bool,string,datetime,array,struct,img,file
export const PROP_DATA_TYPE = new Map([
  ['int', {
    label: '整数',
    specs: ['max', 'min', 'unit']
  }],
  ['float', {
    label: '小数-单精度',
    specs: ['max', 'min', 'unit', 'step',]
  }],
  ['double', {
    label: '小数-双精度',
    specs: ['max', 'min', 'unit', 'step']
  }],
  ['bool', {
    label: '布尔值',
    specs: ['bool0', 'bool1']
  }],
  ['string', {
    label: '字符串',
    specs: ['length']
  }],
  ['datetime', {
    label: '时间',
    specs: ['']
  }],
  ['array', {
    label: '数组',
    specs: ['arraySize', 'itemType']
  }],
  ['struct', {
    label: '结构体',
    specs: ['structSize']
  }],
  ['img', {
    label: '图片',
    specs: ['fileSize', 'fileType']
  }],
  ['file', {
    label: '文件',
    specs: ['fileSize', 'fileType']
  }]
])

export const PROP_DATA_SPECS = new Map([
  ['max', '最大值'],
  ['min', '最小值'],
  ['unit', '单位'],
  ['step', '保留小数'],
  ['arraySize', '数组大小'],
  ['itemType', '数组类型'],
  ['length', '长度'],
  ['fileSize', '文件大小'],
  ['fileType', '文件类型'],
  ['structSize', '结构体大小'],
  ['bool0', '0的值'],
  ['bool1', '1的值'],
])

export const PROP_DATA_ARRAY_TYPE = ['int', 'float', 'double', 'string']

export const PROP_CALL_TYPE = new Map([
  ['SYNC', '同步'],
  ['ASYNC', '异步']
])
