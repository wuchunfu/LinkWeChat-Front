<template>
  <div>
    <div class="card-tab">
      <div class="item" :class="activeIndex === 0 ? 'active' : ''" @click="activeIndex = 0">门店信息</div>
      <div class="item" :class="activeIndex === 1 ? 'active' : ''" @click="activeIndex = 1">数据统计</div>
    </div>
    <div style="margin-top: 10px">
      <template v-if="activeIndex === 0">
        <edit-store :isDetail="true"></edit-store>
      </template>
      <template v-if="activeIndex === 1">
        <store-analyse></store-analyse>
      </template>
    </div>
  </div>
</template>
<script>
import EditStore from './add'
import StoreAnalyse from './store_analyse'
export default {
  name: 'store-detail',
  data() {
    return {
      activeIndex: 0,
    }
  },
  components: {
    EditStore,
    StoreAnalyse,
  },
}
</script>
<style lang="scss" scoped>
.active {
  font-weight: 600;
  color: var(--color) !important;
  background-color: #fff !important;
  box-shadow: 0px 6px 30px 5px rgba(59, 59, 59, 0.05);
}
.card-tab {
  // margin-top: 10px;
  display: flex;
  div {
    width: 88px;
    height: 30px;
    background: #fafafa;
    border-radius: 32px 32px 32px 32px;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.4);
    line-height: 30px;
    text-align: center;
    margin-top: 15px;
    margin-right: 12px;
    margin-bottom: 6px;
  }
  .item:hover {
    cursor: pointer;
    opacity: 0.8;
  }
}
</style>
