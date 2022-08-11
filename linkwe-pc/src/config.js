import { getToken } from '@/utils/auth'
import env from '../env'
// 系统常量
const common = {
  SYSTEM_NAME: 'LinkWeChat',
  SYSTEM_NAME_ALL: 'LinkWechat 企业微信SCRM',
  COMPANY_NAME: '仟微科技',
  COMPANY_NAME_ALL: '仟微科技',
  // LOGO_DARK: env.BASE_URL + 'static/LOGO_DARK.png',
  // LOGO_LIGHT: env.BASE_URL + 'static/LOGO_LIGHT.png',
  // LOGO_TEXT_DARK: env.BASE_URL + 'static/LOGO_TEXT_DARK.png',
  LOGO_TEXT_LIGHT: env.BASE_URL + 'static/LOGO_TEXT_LIGHT.png',
  POST_QRCODE: env.BASE_URL + 'static/POST_QRCODE.png',
  // QRCODE_CONTACT_US: env.BASE_URL + 'static/QRCODE_CONTACT_US.png',
  // QRCODE_CUSTOMER_SERVICE: env.BASE_URL + 'static/QRCODE_CUSTOMER_SERVICE.png',
  COPYRIGHT: 'Copyright © 2018-2022 LinkWeChat All Rights Reserved.',
  IS_PUBLISH_DIALOG: true, // 是否显示发布弹窗
}

window.lwConfig = Object.assign(env, common, window.lwConfig || {})
document.title = window.lwConfig.SYSTEM_NAME_ALL

window.CONFIG = {
  /**
   * 是否系统布局配置
   */
  showSettings: false,

  /**
   * 是否显示 tagsView
   */
  tagsView: true,

  /**
   * 是否固定头部
   */
  fixedHeader: false,

  /**
   * 是否显示logo
   */
  sidebarLogo: true,

  /**
   * @type {string | array} 'production' | ['production', 'development']
   * @description Need show err logs component.
   * The default is only used in the production env
   * If you want to also use it in dev, you can pass ['production', 'development']
   */
  errorLog: 'production',

  services: {
    wecom: '/wecom',
    kf: window.lwConfig.KF_API,
    system: window.lwConfig.SYSTEM_API,
    common: '/common',
  },

  /**
   * 前端生成群活码跳转的H5链接
   * 由于群活码是由前端生成，需要配置一下群活码跳转的链接
   * 例如: http://xxx.xxx.xx/h5/groupCode
   */
  groupCodeH5Link: 'http://linkwechat.cn/mobile/#/groupCode',

  get headers() {
    return { Authorization: 'Bearer ' + getToken() }
  },
}
// 统一为img的src不是绝对地址的拼接接口地址
document.addEventListener(
  'error',
  function (e) {
    let target = e.target
    let src = target.attributes.getNamedItem('src').value
    if (target.tagName.toUpperCase() === 'IMG' && src && !src.includes('http')) {
      target.src = window.lwConfig.BASE_API + src
    }
  },
  true
)

export default window.CONFIG
