<script>
export default {
  name: '',
  components: {},
  data() {
    return {}
  },
  computed: {},
  watch: {},
  created() {},
  mounted() {},
  methods: {},
}
</script>

<template>
  <div class="visitor">
    <!-- <div class="login-top--wrap">
      <div class="login-top--main main-size fcbw">
        <div class="logo-top--wrap">
          <img :src="lwConfig.LOGO_TEXT_DARK" class="logo-top" />
          <a href="https://open.work.weixin.qq.com" target="_blank"><img src="https://open.work.weixin.qq.com/service/img?id=ww38152475d1bca752&t=isp&c=white&s=medium" srcset="
                https://open.work.weixin.qq.com/service/img?id=ww38152475d1bca752&t=isp&c=white&s=medium@2x 2x
              " referrerpolicy="unsafe-url" alt="企业微信" /></a>
        </div>

        <nav class="login-nav--wrap">
          <el-popover placement="top-start" title="" width="80" trigger="hover">
            <a class="nav-li" slot="reference"> 联系我们</a>
            <div class="ac">
              <img :src="lwConfig.QRCODE_CUSTOMER_SERVICE" class="contact-code" alt="" />
              <div class="nav-li">微信扫码咨询</div>
            </div>
          </el-popover>

          <a class="nav-li" :href="lwConfig.AGREEMENT" target="_black">用户协议</a>
          <a class="nav-li" :href="lwConfig.PRIVACY" target="_black">隐私政策</a>
        </nav>
      </div>
    </div> -->

    <router-view></router-view>

    <!--  底部  -->
    <div class="login-footer">
      <span>
        {{ lwConfig.COPYRIGHT || 'Copyright © 2018-2022 LinkWeChat All Rights Reserved.' }}
      </span>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.contact-code {
  width: 80px;
}
.visitor {
  height: 100vh;
  background: #e3ebf6;
  .login-top--wrap {
    background: #fff;
    padding: 5px 20px;
    box-shadow: 0px 5px 5px rgb(215 215 215 / 35%);
  }

  .logo-top--wrap {
    display: flex;
    align-items: center;
    .logo-top {
      max-height: 44px;
      max-width: 170px;
    }
  }

  .login-nav--wrap {
    .nav-li {
      color: #aaa;
      margin-left: 20px;
    }
  }
}

.login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
  // color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
  z-index: 90;
}
</style>
