<script>
import * as api from '@/api/task'

export default {
  name: 'Group',
  data() {
    return {
      query: {
        pageNum: 1,
        pageSize: 10,
        taskName: '',
        startTime: '',
        overTime: '',
        fissionType: 2,
      },
      dateRange: [],
      tableData: [],
      total: 0,
      loading: false,
    }
  },
  created() {
    this.getList()

    this.$store.dispatch(
      'app/setBusininessDesc',
      `
        <div>用于查看当前企业所有的客户列表及详细信息，支持对客户进行打标签。</div>
      `,
    )
  },
  methods: {
    setChange(e) {
      if (e) {
        this.query.startTime = e[0]
        this.query.overTime = e[1]
      } else {
        this.query.startTime = ''
        this.query.overTime = ''
      }
    },
    resetFn() {
      this.query = {
        pageNum: 1,
        pageSize: 10,
        taskName: '',
        startTime: '',
        overTime: '',
        fissionType: 2,
      }
      this.dateRange = []
      this.getList()
    },
    getList(data) {
      this.loading = true
      let params = Object.assign({}, this.query, data)
      api.getList(params).then(({ rows, total }) => {
        this.tableData = rows
        this.total = +total
        this.loading = false
      })
    },
    resetForm() {},
    toDetail(row) {
      this.$router.push({
        path: `detail?id=${row.id}`,
      })
    },
    newAdd() {
      this.$router.push({
        path: 'add',
      })
    },
    toEdit(row) {
      this.$router.push({
        path: `add?id=${row.id}`,
      })
    },
  },
}
</script>

<template>
  <div>
    <el-form ref="queryForm" :inline="true" :model="query" class="top-search">
      <el-form-item label="裂变名" prop="taskName">
        <el-input clearable v-model="query.taskName" placeholder="请输入"></el-input>
      </el-form-item>
      <el-form-item label="添加日期">
        <el-date-picker
          clearable
          v-model="dateRange"
          @change="setChange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          align="right"></el-date-picker>
      </el-form-item>
      <el-form-item label="">
        <el-button
          v-hasPermi="['customerManage:customer:query']"
          type="primary"
          @click="getList({ pageNum: 1 })"
          :loading="loading">
          查询
        </el-button>
        <el-button @click="resetFn">重置</el-button>
        <el-button
          v-hasPermi="['customerManage:customer:query']"
          type="primary"
          style="background: #fa7216; color: #ffffff; border-color: #fa7216"
          @click="newAdd()">
          新增任务
        </el-button>
      </el-form-item>
    </el-form>

    <div class="g-card">
      <el-table :data="tableData">
        <el-table-column prop="taskName" label="群裂变名称"></el-table-column>
        <el-table-column prop="fissNum" label="裂变客户数量"></el-table-column>
        <el-table-column prop="fissStatus" label="活动状态">
          <template slot-scope="{ row }">
            <p>
              {{ { '-1': '失败', 0: '待开始', 1: '进行中', 2: '已结束' }[row.fissStatus] }}
            </p>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="活动时间">
          <template slot-scope="scope">
            <p>{{ scope.row.startTime }} ～ {{ scope.row.overTime }}</p>
          </template>
        </el-table-column>
        <el-table-column prop="operation" label="操作" width="120">
          <template slot-scope="scope">
            <el-button @click="toDetail(scope.row)" type="text">查看</el-button>
            <!-- <el-button v-if="scope.row.fissStatus != 2" @click="toEdit(scope.row)" v-hasPermi="['enterpriseWechat:edit']" size="medium" type="text" icon="el-icon-edit-outline" style="color: #e74e59"></el-button> -->
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="query.pageNum"
        :limit.sync="query.pageSize"
        @pagination="getList()" />
    </div>
  </div>
</template>

<style lang="scss" scoped></style>
