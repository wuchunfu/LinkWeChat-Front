<script>
import MaPage from '@/views/contentCenter/components/MaPage'

export default {
  name: 'Mtext',
  components: { MaPage },
  data() {
    return {
      ids: [], // 选中数组
    }
  },
  watch: {},
  computed: {},
  created() {},
  methods: {
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id)
    },
  },
}
</script>

<template>
  <MaPage ref="page" type="4" :selected="ids" v-slot="{ list }">
    <el-table :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column
        label="文本标题"
        align="center"
        prop="materialName"
        :show-overflow-tooltip="true"
      />
      <el-table-column label="文本内容" align="center" prop="content">
        <template slot-scope="scope">
          <el-tooltip class="item" effect="dark" placement="top">
            <p style="white-space: pre-line" v-html="scope.row.content" slot="content"></p>
            <div class="oneLine">{{ scope.row.content }}</div>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column
        label="发送总次数"
        align="center"
        prop="sendNum"
        :show-overflow-tooltip="true"
      >
        <template slot="header">
          <el-popover placement="top" trigger="hover">
            <div slot="reference">
              发送总次数
              <i class="el-icon-question"></i>
            </div>
            <div>当前素材在侧边栏被员工发送的总次数</div>
          </el-popover>
        </template>
        <template #default="{ row }">
          {{ row.sendNum }}
        </template>
      </el-table-column>
      <el-table-column label="最近更新时间" align="center" width="180px" prop="updateTime">
        <template slot-scope="scope">
          <div>{{ scope.row.updateBy }}</div>
          <span>{{ parseTime(scope.row.updateTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            type="text"
            @click="$refs.page.detail(scope.row)"
            v-hasPermi="['wechat:material:detail']"
            >详情|统计</el-button
          >
          <el-button type="text" @click="$refs.page.edit(scope.row)">编辑</el-button>
          <el-button type="text" @click="$refs.page.remove(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </MaPage>
</template>
<style lang="scss">
.el-tooltip__popper {
  max-width: 500px;
}
.oneLine {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
</style>
