<script>
import * as api from '@/api/customer'
import { getList as getListTag } from '@/api/customer/tag'
import AddTag from '@/components/AddTag'
import SelectTag from '@/components/SelectTag'
import DeleteTag from '@/components/DeleteTag'
import { dictAddType, dictTrackState } from '@/utils/dictionary'
import { getList } from '@/api/salesCenter/businessConver.js'

export default {
  name: 'Customer',
  components: { AddTag, SelectTag, DeleteTag },
  props: {},
  data() {
    return {
      stageList: [],
      query: {
        pageNum: 1,
        pageSize: 10,
        name: '', // "客户名称",
        userIds: '', // "添加人id",
        tagIds: '', // "标签id,多个标签，id使用逗号隔开",
        beginTime: '', // "开始时间",
        endTime: '', // "结束时间"
        customerType: '', //客户类型  1:微信客户;2:企业客户
        trackState: '', //跟进状态 1:待跟进;2:跟进中;3:已成交;4:无意向;5:已流失
        addMethod: '', //添加方式 0:未知来源;1:扫描二维码;2:搜索手机号;3:名片分享;4:群聊;5:手机通讯录;6:微信联系人;7:来自微信好友的添加申请;8:安装第三方应用时自动添加的客服人员;9:搜索邮箱;10:视频号主页添加;11:员工活码;12:新客拉群;13:活动裂变;201:内部成员共享;202:管理员/负责人分配
        gender: '', //0-未知 1-男性 2-女性
        noTagCheck: false,
      },
      queryTag: [], // 搜索框选择的标签
      queryUser: [], // 搜索框选择的添加人
      dateRange: [], // 添加日期
      loading: false,
      total: 0,
      lastSyncTime: '',
      noRepeatCustomerTotal: 0,
      // 添加标签表单
      form: {
        gourpName: '',
        weTags: [],
      },
      list: [], // 客户列表
      staffList: [], // 所有员工列表
      dialogVisible: false, // 选择标签弹窗显隐
      dialogVisibleSelectUser: false, // 选择添加人弹窗显隐
      dialogVisibleAddTag: false, // 添加标签弹窗显隐
      dialogVisibleExtend: false, // 在职继承弹窗显隐
      extendStaff: '', // 在职继承选择的员工
      currentEidt: '', // 当前编辑项
      selectedTag: [], // 选择的标签
      tagDialogType: {
        title: '', // 选择标签弹窗标题
        type: '', // 弹窗类型
      },
      dictCustomerType: Object.freeze({ 1: '微信客户', 2: '企业客户' }),
      dictAddType,
      dictTrackState,
      multiObj: {
        state: 'add', // 'delete'
        title: '批量打标签',
        showDialog: false,
      },
      multipleSelection: [], // 多选数组
      mulSelectedTag: [],
      deleteData: [],
      deleteDialog: false,
    }
  },
  watch: {},
  computed: {},
  created() {
    this.getStage()
    this.getList()
    this.getListTag()

    this.$store.dispatch(
      'app/setBusininessDesc',
      `
        <div>用于查看当前企业所有的客户列表及详细信息，支持对客户进行打标签。</div>
      `,
    )
  },
  mounted() {},
  methods: {
    setValue(data) {
      let str = ''
      this.stageList.forEach((dd) => {
        if (dd.stageVal == data) {
          str = dd.stageKey
        }
      })
      return str
    },
    unique(arr) {
      if (!Array.isArray(arr)) {
        console.log('type error!')
        return
      }
      let res = []
      for (let i = 0; i < arr.length; i++) {
        if (res.indexOf(arr[i]) === -1) {
          res.push(arr[i])
        }
      }
      return res
    },
    openDeleteDialog() {
      let str = ''
      this.multipleSelection.forEach((dd) => {
        if (dd.tagIds) {
          str = str + ',' + dd.tagIds
        }
      })
      this.deleteData = this.unique(str.split(','))
      this.deleteDialog = true
    },
    getDeleteData(data) {
      let arr = []
      this.multipleSelection.forEach((dd) => {
        let obj = {
          externalUserid: dd.externalUserid,
          addTag: data,
          userId: dd.firstUserId,
        }
        arr.push(obj)
      })
      this.loading = true
      api
        .multiMakeTag({ addOrRemove: false, weMakeCustomerTagList: arr })
        .then((res) => {
          this.getList(1)
          this.msgSuccess('批量删除标签成功')
          this.deleteData = []
          this.deleteDialog = false
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },
    multiOperation(state) {
      this.multiObj.state = state
      if (state === 'add') {
        this.multiObj.title = '批量打标签'
      } else {
        this.multiObj.title = '批量删标签'
      }
      this.multiObj.showDialog = true
    },
    getMultiOperation(data) {
      let arr = []
      this.multipleSelection.forEach((dd) => {
        let obj = {
          externalUserid: dd.externalUserid,
          addTag: data,
          userId: dd.firstUserId,
        }
        arr.push(obj)
      })
      this.loading = true
      api
        .multiMakeTag({ addOrRemove: this.multiObj.state === 'add' ? true : false, weMakeCustomerTagList: arr })
        .then((res) => {
          this.getList(1)
          this.msgSuccess(this.multiObj.state === 'add' ? '批量设置标签成功' : '批量删除标签成功')
          this.multiObj.showDialog = false
          this.multipleSelection = []
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },
    getStage() {
      getList().then((res) => {
        this.stageList = res.data
      })
    },
    getList(page) {
      // console.log(this.dateRange);
      if (this.dateRange) {
        this.query.beginTime = this.dateRange[0]
        this.query.endTime = this.dateRange[1]
      } else {
        this.query.beginTime = ''
        this.query.endTime = ''
      }
      page && (this.query.pageNum = page)
      this.query.tagIds = this.queryTag.map((d) => d.tagId) + ''
      this.query.userIds = this.queryUser.map((d) => d.userId) + ''
      this.loading = true
      api
        .getListNew(this.query)
        .then(({ rows, total, lastSyncTime, noRepeatCustomerTotal }) => {
          rows.forEach((element) => {
            if (element.tagIds && element.tagNames) {
              element.tagIds = element.tagIds.split(',')
              element.tagNames = element.tagNames.split(',')
              element.tags = []
              element.tagIds.forEach((unit, index) => {
                element.tags.push({
                  tagId: unit,
                  tagName: element.tagNames[index],
                })
              })
            }
          })
          this.list = rows
          this.total = +total
          this.lastSyncTime = lastSyncTime
          this.noRepeatCustomerTotal = +noRepeatCustomerTotal
          this.loading = false
          this.multipleSelection = []
        })
        .catch(() => {
          this.loading = false
        })
    },
    getListTag(refresh) {
      getListTag().then(({ rows }) => {
        this.listTagOneArray = []
        rows.forEach((element) => {
          element.weTags.forEach((d) => {
            this.listTagOneArray.push(d)
          })
        })
        if (refresh) {
          this.$refs.selectTag.getList()
          this.form = {
            gourpName: '',
            weTags: [],
          }
        }
      })
    },
    showTagDialog() {
      this.query.noTagCheck = false
      this.selectedTag = this.queryTag
      this.tagDialogType = {
        title: '选择标签',
        type: 'query',
      }
      this.dialogVisible = true
      // this.$refs.selectTag.$forceUpdate()
    },
    makeTag(row) {
      this.currentEidt = row
      // if (!this.multipleSelection.length) {
      //   this.msgInfo('请选择一位客户')
      //   return
      // }
      // if (this.multipleSelection.length > 1) {
      //   this.msgInfo('同时只能选择一位客户')
      //   return
      // }
      this.selectedTag = []
      let hasErrorTag = []
      let repeat = []
      row.tags &&
        row.tags.forEach((unit) => {
          // 判断是否有重复标签
          let isRepeat = this.selectedTag.some((d) => {
            return d.tagId === unit.tagId
          })
          // 去重
          if (isRepeat) {
            repeat.push(unit.tagName)
            return
          }
          let filter = this.listTagOneArray.find((d) => {
            return d.tagId === unit.tagId
          })
          // 如果没有匹配到，则说明该便签处于异常状态，可能已被删除或破坏
          if (!filter) {
            hasErrorTag.push(unit.tagName)
            return
          }

          this.selectedTag.push(filter)
        })
      // this.multipleSelection.forEach((element) => {
      // })
      if (hasErrorTag.length > 0) {
        this.$notify({
          // title: '标题名称',
          message: this.$createElement(
            'i',
            { style: 'color: red' },
            '已有标签[' + hasErrorTag + ']不在标签库中，已被删除或存在异常',
          ),
          type: 'warning',
          customClass: 'mzindex',
        })
        // this.msgError('已有标签[' + hasErrorTag + ']不在标签库中，已被删除或存在异常')
      }

      this.tagDialogType = {
        title: '标签设置' + (repeat.length ? '（重复的标签已去重显示）' : ''),
      }
      this.dialogVisible = true
      // this.$refs.selectTag.$forceUpdate()
    },
    sync() {
      // const loading = this.$loading({
      //   lock: true,
      //   text: 'Loading',
      //   spinner: 'el-icon-loading',
      //   background: 'rgba(0, 0, 0, 0.7)',
      // })
      api
        .sync()
        .then(() => {
          // loading.close()
          this.getList(1)
          this.msgSuccess('后台开始同步数据，请稍后关注进度')
        })
        .catch((fail) => {
          // loading.close()
          console.log(fail)
        })
    },
    // /** 导出按钮操作 */
    // exportCustomer() {
    //   const queryParams = this.query
    //   this.$confirm('是否确认导出所有客户数据项?', '警告', {
    //     confirmButtonText: '确定',
    //     cancelButtonText: '取消',
    //     type: 'warning'
    //   })
    //     .then(function() {
    //       return api.exportCustomer(queryParams)
    //     })
    //     .then((response) => {
    //       this.download(response.msg)
    //     })
    //     .catch(function() {})
    // },
    selectedUser(list) {
      this.queryUser = list
    },
    submitSelectTag(selected) {
      if (this.tagDialogType.type === 'query') {
        this.queryTag = selected
        this.dialogVisible = false
      } else {
        let data = {
          externalUserid: this.currentEidt.externalUserid,
          addTag: selected,
          userId: this.currentEidt.firstUserId,
        }
        // let apiType = {
        //   add: 'makeLabel',
        //   remove: 'removeLabel'
        // }
        this.loading = true
        api
          .makeLabel(data)
          .then(() => {
            this.msgSuccess('操作成功')
            this.dialogVisible = false
            this.getList()
            this.loading = false
          })
          .catch(() => {
            this.loading = false
          })
      }
    },
    resetForm(formName) {
      this.dateRange = []
      this.queryTag = []
      this.queryUser = []
      this.query.noTagCheck = false
      this.$refs['queryForm'].resetFields()
      this.getList(1)
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.multipleSelection = selection
    },
    // 用于多选变单选
    handleSelection(selection, row) {
      this.$nextTick(() => {
        this.$refs.table.clearSelection()
        this.$refs.table.toggleRowSelection(row)
      })
    },
    goRoute(row) {
      let { externalUserid, firstUserId: userId } = row
      this.$router.push({
        path: 'customerDetail',
        query: { externalUserid, userId },
      })
    },

    // 在职继承
    async transfer(row) {
      await this.$refs['SelectStaffForm'].validate()
      // if (!this.extendStaff) {
      //   this.msgError('请选择员工')
      //   return
      // }
      let data = {
        handoverUserId: this.currentEidt.firstUserId, //移交人
        takeoverUserId: this.extendStaff, //接受人
        externalUserid: this.currentEidt.externalUserid, //客户id，多个客户使用逗号隔开
      }
      api.jobExtends(data).then((res) => {
        this.msgSuccess('操作成功')
        this.dialogVisibleExtend = false
        this.getList()
      })
    },
    setData(data) {
      this.queryTag = []
      this.query.tagIds = ''
    },
  },
}
</script>

<template>
  <div v-loading="loading">
    <el-form ref="queryForm" :inline="true" :model="query" label-width="70px" class="top-search">
      <el-form-item label="客户名称" prop="name">
        <el-input v-model="query.name" placeholder="请输入"></el-input>
      </el-form-item>
      <el-form-item label="客户类型" prop="customerType">
        <el-select :popper-append-to-body="false" v-model="query.customerType" placeholder="请选择">
          <el-option v-for="(item, index) in dictCustomerType" :key="index" :label="item" :value="index"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="跟进员工" prop="customerType">
        <div class="tag-input" @click="dialogVisibleSelectUser = true">
          <span class="tag-place" v-if="!queryUser.length">请选择</span>
          <template v-else>
            <el-tag v-for="(unit, unique) in queryUser" :key="unique">{{ unit.name }}</el-tag>
          </template>
        </div>
      </el-form-item>
      <el-form-item label="商机阶段" prop="trackState">
        <el-select :popper-append-to-body="false" v-model="query.trackState" placeholder="请选择">
          <el-option
            v-for="(item, index) in stageList"
            :key="index"
            :label="item.stageKey"
            :value="item.stageVal"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="添加方式" prop="addMethod">
        <el-select :popper-append-to-body="false" v-model="query.addMethod" placeholder="请选择">
          <el-option v-for="(item, index) in dictAddType" :key="index" :label="item" :value="index"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="添加日期">
        <el-date-picker
          v-model="dateRange"
          value-format="yyyy-MM-dd"
          type="daterange"
          :picker-options="pickerOptions"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          align="right"></el-date-picker>
      </el-form-item>
      <el-form-item label="客户标签" prop="customerType">
        <div class="tag-input" @click="showTagDialog">
          <span class="tag-place" v-if="!queryTag.length">请选择</span>
          <template v-else>
            <el-tag v-for="(unit, unique) in queryTag" :key="unique">{{ unit.name }}</el-tag>
          </template>
        </div>
      </el-form-item>
      <el-form-item>
        <el-checkbox v-model="query.noTagCheck" @change="setData">无标签客户</el-checkbox>
      </el-form-item>

      <el-form-item label="">
        <!-- v-hasPermi="['customerManage:customer:query']" -->
        <el-button type="primary" @click="getList(1)">查询</el-button>
        <el-button @click="resetForm()">重置</el-button>
        <!-- v-hasPermi="['customerManage:customer:query']" -->
        <!-- <el-button
          v-hasPermi="['customerManage:customer:export']"
          type="info"
          @click="exportCustomer"
          >导出列表</el-button
        > -->
      </el-form-item>
    </el-form>

    <div class="g-card">
      <div class="g-card-title mid-action">
        <div>
          共
          <span class="g-color">{{ noRepeatCustomerTotal }}</span>
          位客户（去重）
        </div>
        <!-- 共
        <span class="g-color">{{ total }}</span> 位客户，实际客户
        <span class="g-color">{{ total }}</span> 位。 -->
        <div>
          <span class="desc">最近同步：{{ lastSyncTime }}</span>
          <ButtonSync :lastSyncTime="lastSyncTime" @click="sync">同步客户</ButtonSync>
          <!-- <el-button v-hasPermi="['customerManage:customer:checkRepeat']" type="primary">查看重复客户</el-button> -->
          <el-button class="ml10" type="primary" :disabled="!multipleSelection.length" @click="multiOperation('add')">
            批量打标签
          </el-button>
          <el-button type="primary" :disabled="!multipleSelection.length" @click="openDeleteDialog()">
            批量删标签
          </el-button>
        </div>
      </div>

      <el-table
        ref="table"
        :data="list"
        tooltip-effect="dark"
        highlight-current-row
        @selection-change="handleSelectionChange">
        <!-- -->
        <el-table-column type="selection" align="center" width="55"></el-table-column>
        <el-table-column label="客户" prop="customerName" header-align="center" align="" width="180">
          <template slot-scope="{ row }">
            <div class="cp flex aic" @click="goRoute(row)">
              <el-image class="avatar" :src="row.avatar" fit="fit"></el-image>
              <div class="ml10">
                <p class="blod">{{ row.customerName }}</p>
                <i :class="['el-icon-s-custom', { 1: 'man', 2: 'woman' }[row.gender]]"></i>
                <span :style="{ color: row.customerType === 1 ? '#4bde03' : '#f9a90b' }">
                  {{ { 1: '@微信', 2: '@企业微信' }[row.customerType] }}
                </span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="tagNames" label="客户标签" align="center" width="220">
          <div v-if="row.tagNames" slot-scope="{ row }">
            <TagEllipsis :list="row.tagNames"></TagEllipsis>
          </div>
          <span v-else>无标签</span>
        </el-table-column>
        <!-- <el-table-column prop="corpName" label="公司名称" align="center"></el-table-column> -->
        <el-table-column prop="userName" label="跟进员工" align="center">
          <!-- <template slot-scope="{ row }">{{
          row.weFlowerCustomerRels[0] ? row.weFlowerCustomerRels[0].userName : ''
        }}</template> -->
        </el-table-column>
        <el-table-column prop="trackState" label="商机阶段" align="center">
          <template slot-scope="{ row }">
            <!-- <el-tag v-if="row.trackState" :type="dictTrackState[~~row.trackState + ''].color">
              {{ dictTrackState[~~row.trackState + ''].name }}
            </el-tag> -->
            <el-tag v-if="row.trackState">
              {{ setValue(row.trackState) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="addMethod" label="添加方式" align="center">
          <template slot-scope="{ row }">{{ dictAddType[row.addMethod + ''] }}</template>
        </el-table-column>
        <el-table-column prop="firstAddTime" label="添加时间" align="center">
          <!-- <template slot-scope="{ row }">{{
          row.weFlowerCustomerRels[0] ? row.weFlowerCustomerRels[0].createTime : ''
        }}</template> -->
        </el-table-column>

        <el-table-column label="操作" width="200" align="center">
          <template slot-scope="{ row }">
            <!-- v-hasPermi="['customerManage:customer:view']" -->
            <el-button @click="goRoute(row)" type="text">查看</el-button>
            <el-button type="text" @click="makeTag(row)">标签管理</el-button>
            <!-- v-hasPermi="['customerManage/customer:makeTag']" -->
            <el-button
              type="text"
              :disabled="row.isTransfer"
              @click="
                dialogVisibleExtend = true
                currentEidt = row
              ">
              在职继承
            </el-button>
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

    <!-- 选择标签弹窗 -->
    <SelectTag
      ref="selectTag"
      :visible.sync="dialogVisible"
      :title="tagDialogType.title"
      :defaultValues="selectedTag"
      @success="submitSelectTag">
      <!-- <el-button class="ml20" type="primary" @click="dialogVisibleAddTag = true"
        >添加标签</el-button
      > -->
    </SelectTag>

    <!-- 选择添加人弹窗 -->
    <SelectWeUser :visible.sync="dialogVisibleSelectUser" title="选择添加人" @success="selectedUser"></SelectWeUser>

    <!-- 添加标签弹窗 -->
    <AddTag :visible.sync="dialogVisibleAddTag" :form="form" @success="getListTag(true)" />

    <!-- 在职继承弹窗 -->
    <el-dialog title="选择员工" :visible.sync="dialogVisibleExtend" :close-on-click-modal="false">
      <SelectStaffForm ref="SelectStaffForm" :selected.sync="extendStaff"></SelectStaffForm>
      <div slot="footer">
        <el-button @click="dialogVisibleExtend = false">取 消</el-button>
        <el-button type="primary" @click="transfer">确 定</el-button>
      </div>
    </el-dialog>

    <SelectTag
      v-if="multiObj.showDialog"
      ref="multiTag"
      :selected="mulSelectedTag"
      :visible.sync="multiObj.showDialog"
      :title="multiObj.title"
      @success="getMultiOperation"></SelectTag>
    <DeleteTag
      v-if="deleteDialog"
      ref="deleteTag"
      :visible.sync="deleteDialog"
      :initList="deleteData"
      :selected="deleteData"
      @success="getDeleteData"></DeleteTag>
  </div>
</template>

<style lang="scss" scoped>
.avatar {
  width: 56px;
  height: 56px;
  flex: none;
  border-radius: var(--border-radius-big);
}
.el-icon-s-custom {
  font-size: 16px;
  // margin-left: 4px;
  color: #999;
  &.man {
    color: #13a2e8;
  }
  &.woman {
    color: #f753b2;
  }
}
.bfc-d + .bfc-d .el-checkbox:first-child {
  margin-left: 10px;
}
</style>
<style>
.mzindex {
  z-index: 5000 !important;
}
</style>
