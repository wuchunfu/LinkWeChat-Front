<template>
  <!-- <el-scrollbar class="page-scrollbar"> -->
  <div class="app-main">
    <transition name="fade-transform" mode="out-in" v-if="!isActiveMicroApp">
      <router-view class="page container" :key="key" />
      <!-- <keep-alive :include="cachedViews">
      </keep-alive> -->
    </transition>
    <div v-loading="loading" class="height100 micro-app-wrap container">
      <div id="micro-app" class="height100" v-show="isActiveMicroApp">
        <!-- <div id="app" class="mask" style="position: absolute">
        <i class="el-icon-loading cc"></i>
      </div> -->
      </div>
    </div>
  </div>
  <!-- </el-scrollbar> -->
</template>

<script>
import { registerMicroApps, start, initGlobalState } from 'qiankun'
export default {
  name: 'AppMain',
  data() {
    return {
      loading: false,
    }
  },
  computed: {
    cachedViews() {
      return this.$store.state.tagsView.cachedViews
    },
    key() {
      return this.$route.path
    },
    isActiveMicroApp() {
      let microApp = window.lwConfig.MICRO_APPS
      return (
        microApp && Object.values(microApp).some((item) => this.$route.path.startsWith(item.activeRule.match('/.*')[0]))
      )
    },
  },
  mounted() {
    if (window.lwConfig.MICRO_APPS && window.lwConfig.MICRO_APPS.store) {
      window.Vue2 = window.Vue
      delete window.Vue
      window.lwConfig.MICRO_APPS.store.loader = (loading) => (this.loading = loading)
      registerMicroApps([window.lwConfig.MICRO_APPS.store])
      start({
        sandbox: { experimentalStyleIsolation: true },
      })

      // 挂在window供下面微服务使用
      window.lwConfig.$store = this.$store

      // 初始化 state
      // const actions = initGlobalState({ token: getToken() })
      // actions.onGlobalStateChange((state, prev) => {
      //   // state: 变更后的状态; prev 变更前的状态
      // });
      // actions.setGlobalState(state)
    }
    // if (window.lwConfig.MICRO_APPS) {
    //   import('qiankun').then(({ registerMicroApps, start }) => {
    //     registerMicroApps(window.lwConfig.MICRO_APPS)
    //     start({
    //       sandbox: { experimentalStyleIsolation: true },
    //     })
    //   })
    // }
  },
}
</script>

<style lang="scss" scoped>
// ::v-deep .el-scrollbar__wrap {
//   margin: 20px 0;
//   padding: 0 20px;
// }
.app-main {
  position: relative;
  overflow: hidden;
  flex: auto;
  margin-top: 20px;
}
.container {
  position: relative;
  max-height: 100%;

  overflow-y: auto;
  overflow-x: hidden;
  &::-webkit-scrollbar-thumb {
    visibility: hidden;
  }
  &:hover::-webkit-scrollbar-thumb {
    visibility: visible;
  }
}
.page {
  padding: 0 20px;
  // background: #fff;
  // border-radius: var(--border-radius-big);
}
.micro-app-wrap {
  margin: 0 20px;
}
.fixed-header + .app-main {
  padding-top: 50px;
}

.hasTagsView {
  .app-main {
    /* 84 = navbar + tags-view = 50 + 34 */
    height: calc(100vh - 84px);
  }

  .fixed-header + .app-main {
    padding-top: 84px;
  }
}
</style>

<style lang="scss">
// fix css style bug in open el-dialog
.el-popup-parent--hidden {
  .fixed-header {
    padding-right: 15px;
  }
}
#micro-app > div {
  height: 100%;
}
</style>
