<template>
  <el-table :data="dataList" ref="singleTable">
    <!-- <el-table-column type="index" width="50" label="序号" align="center"></el-table-column> -->
    <el-table-column prop="name" label="素材" align="left">
      <template slot-scope="{ row }">
        <el-tooltip
          :content="row.materialName"
          placement="top"
          :disabled="row.materialName ? row.materialName.length < 12 : true"
        >
          <span class="title">{{ coverContent(row.materialName, 12) }}</span>
        </el-tooltip>
        <!-- 文本 -->
        <div v-if="row.mediaType === '4'">
          <el-tooltip placement="top" :disabled="row.content ? row.content.length < 50 : true">
            <p style="white-space: pre-line" v-html="row.content" slot="content"></p>
            <span class="twosplice">{{ coverContent(row.content, 50) }}</span>
          </el-tooltip>
        </div>
        <!-- 图片 -->
        <div v-if="row.mediaType === '0'">
          <el-image :src="row.materialUrl" fit="contain" class="imgsize"></el-image>
        </div>
        <!-- 图文 -->
        <div v-if="row.mediaType === '9'" style="display: flex">
          <el-image
            :src="row.coverUrl"
            fit="contain"
            class="imgsize"
            v-if="row.coverUrl"
          ></el-image>
          <div class="icon-style" v-else>
            <svg-icon class="icon-style" iconClass="imgText"></svg-icon>
          </div>
          <el-tooltip
            :content="row.content"
            placement="top"
            :disabled="row.content ? row.content.length < 50 : true"
          >
            <span class="twosplice distStyle">{{ coverContent(row.content, 50) }}</span>
          </el-tooltip>
        </div>
        <!-- 小程序 -->
        <div v-if="row.mediaType === '11'">
          <el-image :src="row.coverUrl" fit="contain" class="imgsize"></el-image>
        </div>
        <!-- 文章 -->
        <div v-if="row.mediaType === '12'" style="display: flex">
          <el-image
            :src="row.coverUrl"
            fit="contain"
            class="imgsize"
            v-if="row.coverUrl"
          ></el-image>
          <div class="icon-style" v-else>
            <svg-icon class="icon-style" iconClass="article"></svg-icon>
          </div>
          <el-tooltip
            :content="row.digest"
            placement="top"
            :disabled="row.digest ? row.digest.length < 50 : true"
          >
            <span class="twosplice distStyle">{{ coverContent(row.digest, 50) }}</span>
          </el-tooltip>
        </div>
        <!-- 视频 -->
        <div v-if="row.mediaType === '2'" style="display: flex">
          <el-image :src="row.coverUrl" fit="contain" class="imgsize"></el-image>
          <el-tooltip
            :content="row.digest"
            placement="top"
            :disabled="row.digest ? row.digest.length < 50 : true"
          >
            <span class="twosplice distStyle">{{ coverContent(row.digest, 50) }}</span>
          </el-tooltip>
        </div>
        <!-- 文件 -->
        <div v-if="row.mediaType === '3'" style="display: flex">
          <!-- <el-image :src="row.coverUrl" fit="contain" class="imgsize"></el-image> -->
          <div class="icon-style" v-if="row.materialUrl">
            <svg-icon
              class="icon-style"
              :iconClass="row.materialUrl ? filType(row.materialUrl) : ''"
            ></svg-icon>
          </div>
          <el-tooltip
            :content="row.digest"
            placement="top"
            :disabled="row.digest ? row.digest.length < 50 : true"
          >
            <span class="twosplice distStyle">{{ coverContent(row.digest, 50) }}</span>
          </el-tooltip>
        </div>
        <!-- 海报 -->
        <div v-if="row.mediaType === '5'" style="display: flex">
          <el-image :src="row.materialUrl" fit="contain" class="imgsize"></el-image>
          <el-tooltip
            :content="row.digest"
            placement="top"
            :disabled="row.digest ? row.digest.length < 50 : true"
          >
            <span class="twosplice distStyle">{{ coverContent(row.digest, 50) }}</span>
          </el-tooltip>
        </div>
      </template>
    </el-table-column>
    <el-table-column prop="mediaType" width="50" label="类型" align="center">
      <template slot-scope="{ row }">
        <div>
          {{ deltype(row.mediaType) }}
        </div>
      </template>
    </el-table-column>
    <el-table-column prop="height" label="操作" align="center">
      <template slot-scope="scope">
        <el-button type="text">
          <el-button type="text">
            <el-button type="text" v-if="['2', '5', '9'].includes(scope.row.mediaType)">
              <!-- 视频,海报,图文 -->
              <a :href="scope.row.materialUrl" target="_blank">预览</a></el-button
            >
            <!-- 文件 -->
            <el-button type="text" v-if="scope.row.mediaType == '3'">
              <a :href="dealUrl(scope.row.materialUrl)" target="_blank">预览</a>
            </el-button>
            <!-- 文章 -->
            <el-button type="text" v-if="scope.row.mediaType == '12'" @click="preview(scope.row)">
              <span>预览</span>
            </el-button></el-button
          ></el-button
        >
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
export default {
  data() {
    return {}
  },
  props: {
    dataList: {
      type: Array,
    },
  },
  methods: {
    dealUrl(url) {
      return window.lwConfig.PRIVIEW_URL + encodeURIComponent(this.base64Encode(url))
    },
    base64Encode(str) {
      if (str === undefined || str === '' || str === null) {
        return str
      }
      return btoa(
        encodeURIComponent(str).replace(/%([0-9A-F]{2})/g, function toSolidBytes(match, p1) {
          return String.fromCharCode('0x' + p1)
        })
      )
    },
    preview(row) {
      let routeData = this.$router.resolve({
        path: '/preview',
        query: { obj: encodeURIComponent(JSON.stringify(row)) },
      })
      window.open(routeData.href, '_blank')
    },
    // 超过num个。。。展示
    coverContent(str, num) {
      if (str && str.length > num) {
        str = str.substr(0, num) + '...'
      }
      return str
    },
    filType(file) {
      let filecontent = JSON.parse(JSON.stringify(file))
      filecontent = filecontent.split('.')
      let type = filecontent[filecontent.length - 1]
      if (type === 'pdf') {
        return 'pdf'
      } else if (['doc', 'docx'].includes(type)) {
        return 'word'
      } else if (['ppt', 'pptx', 'pps', 'pptsx'].includes(type)) {
        return 'ppt'
      } else {
        return ''
      }
    },
    deltype(type) {
      switch (type) {
        case '4':
          return '文本'
          break
        case '0':
          return '图片'
          break
        case '9':
          return '图文'
          break
        case '11':
          return '小程序'
          break
        case '12':
          return '文章'
          break
        case '2':
          return '视频'
          break
        case '3':
          return '文件'
          break
        case '5':
          return '海报'
          break
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.icon-style {
  width: 50px;
  height: 50px;
}
.title2 {
  width: 80%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: bold;
  color: #aaa;
  font-size: 12px;
}
.imgsize {
  width: 50px;
  height: 50px;
  margin-right: 10px;
}
</style>
