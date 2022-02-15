import request from '@/utils/request'
const service = window.CONFIG.services.system + window.CONFIG.services.wecom + '/RedEnvelopes'

/**
 * 员工限额列表获取
 * @param {*} params
{
pageNum	是 当前页
pageSize	是 每页显示条数
userId	是 检索条件，传员工id
}
 */
export function getList(params) {
  return request({
    url: service + '/findLimitUserRedEnvelops',
    params,
  })
}

/**
 * 新建或编辑员工限额
 * @param {*} params
{
    "id":1483367396696686593,//存在则传，不存在则传null
    "userId": "haon", //员工id
    "singleCustomerReceiveNum": 39, //单日每员工发红包次数
    "singleCustomerReceiveMoney": 23.94 //单日每员工发红包总额
}
 */
export function addOrUpdate(params) {
  return request({
    url: service + '/addOrUpdateUserRedEnvelopsLimit',
    method: 'post',
    data: params,
  })
}

/**
 * 批量更新或编辑员工限额
 * @param {*} params
{
    "ids": "1483367396696686593,1483369708055994370",//id,多个使用逗号隔开
    "singleCustomerReceiveNum": 90,//单日每员工发红包次数
    "singleCustomerReceiveMoney": 90 //单日每员工发红包总额
}
 */
export function batchUpdate(params) {
  return request({
    url: service + '/batchUpdateUserRedEnvelopsLimit',
    method: 'post',
    data: params,
  })
}

/**
 * 员工管理删除
 * @param {*} ids 多个id，使用逗号隔开
 * @returns
 */
export function remove(ids) {
  return request({
    url: service + '/deleteLimitUserRedEnvelops/' + ids,
    method: 'DELETE',
  })
}
