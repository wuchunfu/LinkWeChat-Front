<script>
import { checkByCondition } from '@/api/customer/index'
import * as taskApi from '@/api/task'
import SelectTag from '@/components/SelectTag'
import SelectPoster from '@/components/SelectPoster'
import { validURL } from '@/utils/validate'
import { dateFormat } from '@/utils/index'
const validateUrl = (rule, value, callback) => {
  if (value !== '' && !validURL(value)) {
    callback(new Error('链接格式不正确'))
  } else {
    callback()
  }
}
export default {
  name: 'editTask',
  components: {
    SelectTag,
    SelectPoster,
  },
  data() {
    return {
      query: {
        taskName: '',
        fissInfo: '',
        fissNum: '',
        dateRange: '',
      },
      groupForm: {
        taskFissionStaffs: [],
        sendType: 0,
        customerTag: [],
        customerTagId: [],
        tagType: 0,
        postersId: '',
        postersUrl: '',
        fissionTarget: '',
        fissionTargetId: '',
        welcomeMsg: '',
        rewardUrl: '',
        rewardImageUrl: '',
        rewardRule: '',
      },
      ruleForm: {
        taskName: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
        postersId: [{ required: true, message: '请输入海报', trigger: 'blur' }],
        fissionTargetId: [{ required: true, message: '请选择员工', trigger: 'blur' }],
        fissNum: [
          { required: true, message: '请输入数量', trigger: 'blur' },
          { pattern: /^[1-9]\d*$/, message: '请输入数字', trigger: 'blur' },
        ],
        dateRange: [{ required: true, message: '时间不可为空', trigger: 'blur' }],
        rewardUrl: [{ validator: validateUrl, trigger: 'blur' }],
      },
      dialogVisibleSelectUser: false,
      dialogVisibleSelectTag: false,
      dialogVisibleSelectPoster: false,
      dialogVisibleTargetStff: false,
      selectTagType: '',
      pageType: 'add',
      taskDetail: {},
      pickerOptions: {
        disabledDate(time) {
          let data = Date.now() - 8.64e7
          return time.getTime() < data
        },
        // selectableRange: '00:00:00 - 23:59:59'
      },
    }
  },
  created() {
    if (this.$route.query.id) {
      this.pageType = 'edit'
      this.getList(this.$route.query.id)
    }
  },
  watch: {
    'query.dateRange': {
      handler(newValue, oldValue) {
        if (newValue[0]) {
          let date = new Date()
          let min = date.getMinutes()
          date.setMinutes(min + 1)
          let nowDate = dateFormat(date, 'hh:mm:ss')
          let st = ''
          if (dateFormat(date, 'yyyy-MM-dd') === dateFormat(newValue[0], 'yyyy-MM-dd')) {
            let hh1 = dateFormat(newValue[0], 'hh:mm:ss')
            if (hh1 < nowDate) {
              this.query.dateRange[0] = dateFormat(new Date(), 'yyyy-MM-dd hh:mm:ss')
            }
            st = nowDate
          } else {
            st = '00:00:00'
          }
          //  this.pickerOptions.selectableRange = st + ' - 23:59:59'
          //  this.pickerOptions = this.pickerOptions
        }
      },
    },
  },
  mounted() {},
  methods: {
    submitForm() {
      let conditionParams = {}
      try {
        let basciForm = new Promise((resolve, reject) => {
          this.$refs['ruleForm'].validate((valid) => {
            if (valid) resolve()
          })
        })
        let posterForm = new Promise((resolve, reject) => {
          this.$refs['fissTarget'].validate((valid) => {
            if (valid) resolve()
          })
        })
        Promise.all([basciForm, posterForm])
          .then(() => {
            let params = {
              ...this.query,
              ...this.groupForm,
            }
            params.startTime = params.dateRange[0]
            params.overTime = params.dateRange[1]
            delete params.dateRange
            //发送成员选择全部
            if (!params.sendType) {
              params.taskFissionStaffs = [
                {
                  staffId: '',
                  staffName: '',
                  staffType: 3,
                },
              ]
            } else {
              conditionParams.userIds = []
              params.taskFissionStaffs.forEach((e) => e.staffType == 2 && conditionParams.userIds.push(e.staffId))
              conditionParams.userIds += ''

              conditionParams.deptIds = []
              params.taskFissionStaffs.forEach((e) => e.staffType == 1 && conditionParams.deptIds.push(e.staffId))
              conditionParams.deptIds += ''
            }
            delete params.sendType
            //客户标签选择全部
            if (!params.tagType) {
              params.customerTag = ''
              params.customerTagId = 'all'
            } else {
              params.customerTag = params.customerTag.join(',')
              conditionParams.tagIds = params.customerTagId = params.customerTagId.join(',')
            }
            delete params.tagType
            params.fissionType = 1

            checkByCondition(conditionParams).then(({ data }) => {
              if (!data.customerNum) {
                return this.msgError('执行失败，没有可执行客户')
              }
              this.addOrEditTask(params)
            })
          })
          .catch((error) => {
            console.log(error)
          })
      } catch (error) {
        console.log(error)
      }
    },
    addOrEditTask(params) {
      let type = 'addTask'
      if (this.pageType == 'edit') {
        params.id = this.$route.query.id
        type = 'editTask'
      }
      taskApi[type](params).then(({ code }) => {
        if (code == 200) {
          this.msgSuccess('操作成功')
          // taskApi.sendFission(JSON.parse(res.msg).id)
          this.$router.back()
        }
      })
    },
    changeType() {
      // console.log(this.groupForm.sendType)
      // this.groupForm.taskFissionStaffs = []
    },
    getList(id) {
      taskApi.getTaskDetail(id).then((res) => {
        this.taskDetail = res.data
        let query = {
          taskName: res.data.taskName,
          fissInfo: res.data.fissInfo,
          fissNum: res.data.fissNum,
          dateRange: [res.data.startTime + ' 00:00:00', res.data.overTime + ' 00:00:00'],
        }
        this.query = query
        this.groupForm = {
          taskFissionStaffs: res.data.taskFissionStaffs,
          sendType: res.data.taskFissionStaffs.length && res.data.taskFissionStaffs[0].staffId ? 1 : 0,
          customerTag: res.data.customerTagId == 'all' ? '' : res.data.customerTag.split(','),
          customerTagId: res.data.customerTagId == 'all' ? 'all' : res.data.customerTagId.split(','),
          tagType: res.data.customerTagId == 'all' ? 0 : 1,
          postersId: res.data.postersId,
          postersUrl: res.data.postersUrl,
          fissionTarget: res.data.fissionTarget,
          fissionTargetId: res.data.fissionTargetId,
          welcomeMsg: res.data.welcomeMsg,
          rewardUrl: res.data.rewardUrl,
          rewardImageUrl: res.data.rewardImageUrl,
          rewardRule: res.data.rewardRule,
        }
      })
    },
    changeTagType() {},
    selectedUser(value) {
      this.groupForm.taskFissionStaffs = value.map((item) => {
        let selectParam = {}
        if (item.isParty) {
          // 部门
          selectParam = {
            staffId: item.userId,
            staffName: item.name,
            staffType: 1,
          }
        } else {
          // 人员
          selectParam = {
            staffId: item.userId,
            staffName: item.name,
            staffType: 2,
          }
        }
        return selectParam
      })
    },
    submitSelectTag(value) {
      let tagIds = [],
        tagName = []
      value.forEach((item) => {
        tagIds.push(item.tagId)
        tagName.push(item.name)
      })
      this.groupForm.customerTag = tagName
      this.groupForm.customerTagId = tagIds
    },
    submitSelecPoster(value) {
      this.groupForm.postersId = value.id
      this.groupForm.postersUrl = value.materialUrl
    },
    tagetSelect(value) {
      if (value.length) {
        this.groupForm.fissionTarget = value[0].name
        this.groupForm.fissionTargetId = value[0].userId
      } else {
        this.groupForm.fissionTarget = ''
        this.groupForm.fissionTargetId = ''
      }
    },
    insertCustomer() {
      this.groupForm.welcomeMsg += '#客户昵称#'
    },
  },
}
</script>

<template>
  <div class="task-edit-container">
    <div class="g-card">
      <h4 class="g-card-title">基本信息设置</h4>
      <el-form :model="query" :rules="ruleForm" ref="ruleForm" label-width="120px" labelPosition="left">
        <el-form-item label="目标活动名称" prop="taskName">
          <el-input v-model="query.taskName" placeholder="请输入"></el-input>
        </el-form-item>
        <el-form-item label="裂变引导语" prop="fissInfo">
          <el-input v-model="query.fissInfo" placeholder="请输入裂变引导语(最多255字)" maxlength="255"></el-input>
        </el-form-item>
        <el-form-item label="裂变客户数量" prop="fissNum">
          <el-input v-model="query.fissNum"></el-input>
        </el-form-item>
        <el-form-item label="活动时间" prop="dateRange">
          <el-date-picker
            v-model.trim="query.dateRange"
            :picker-options="pickerOptions"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            align="right"
            value-format="yyyy-MM-dd HH:mm:ss"></el-date-picker>
        </el-form-item>
      </el-form>
    </div>
    <div class="g-card">
      <h4 class="g-card-title">活动发起成员</h4>
      <el-form :model="query" :rules="ruleForm" label-width="120px" ref="fissStaff" labelPosition="left">
        <el-form-item label="发起成员" prop="taskNames">
          <el-radio-group v-model="groupForm.sendType" @change="changeType">
            <el-radio :label="0">全部</el-radio>
            <el-radio :label="1">选择成员</el-radio>
          </el-radio-group>
          <div v-if="groupForm.sendType">
            <el-tag v-for="item in groupForm.taskFissionStaffs" :key="item.staffId">{{ item.staffName }}</el-tag>
          </div>
        </el-form-item>
        <el-form-item label="" v-if="groupForm.sendType">
          <el-button
            type="primary"
            class="ml10"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="dialogVisibleSelectUser = true">
            选择成员
          </el-button>
        </el-form-item>
        <el-form-item label="客户标签" prop="tagType">
          <el-radio-group v-model="groupForm.tagType" @change="changeTagType">
            <el-radio :label="0">全部</el-radio>
            <el-radio :label="1">选择标签</el-radio>
          </el-radio-group>
          <div v-if="groupForm.tagType">
            <el-tag v-for="item in groupForm.customerTag" :key="item">{{ item }}</el-tag>
          </div>
        </el-form-item>
        <el-form-item label="" v-if="groupForm.tagType">
          <el-button
            type="primary"
            class="ml10"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="dialogVisibleSelectTag = true">
            选择标签
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="g-card">
      <h4 class="g-card-title">裂变海报</h4>
      <el-form :model="groupForm" :rules="ruleForm" ref="fissTarget" label-width="120px" labelPosition="left">
        <el-form-item label="选择海报" prop="postersId">
          <el-button
            type="primary"
            class="ml10"
            plain
            icon="el-icon-plus"
            @click="dialogVisibleSelectPoster = true"
            v-if="!groupForm.postersId">
            选择海报
          </el-button>
          <div v-if="groupForm.postersId" class="changePosterBody">
            <img :src="groupForm.postersUrl" class="postersUrl" />
            <span class="changeUrl" @click="dialogVisibleSelectPoster = true">修改</span>
          </div>
        </el-form-item>

        <el-form-item label="添加员工" prop="fissionTargetId">
          <el-button
            type="primary"
            class="ml10"
            plain
            icon="el-icon-plus"
            v-if="!groupForm.fissionTargetId"
            @click="dialogVisibleTargetStff = true">
            添加员工
          </el-button>
          <div v-if="groupForm.fissionTargetId" class="changePosterBody">
            <el-tag>{{ groupForm.fissionTarget }}</el-tag>
            <span class="changeUrl" @click="dialogVisibleTargetStff = true">修改</span>
          </div>
          <p class="targetTips">
            选择员工，用于此次任务裂变的目标添加人。
            <!-- 如不选择将默认使用本次活动下发起员工的活码信息。 -->
          </p>
        </el-form-item>
      </el-form>
    </div>
    <div class="g-card">
      <h4 class="g-card-title">活动奖励</h4>
      <el-form :model="groupForm" :rules="ruleForm" ref="reward" label-width="120px" labelPosition="left">
        <el-form-item label="兑奖链接" prop="rewardUrl">
          <el-input v-model="groupForm.rewardUrl" placeholder="请输入"></el-input>
        </el-form-item>
        <el-form-item label="兑奖图片" prop="rewardImageUrl">
          <upload class="image-uploader" :fileUrl.sync="groupForm.rewardImageUrl" type="0"></upload>
        </el-form-item>
        <el-form-item label="兑奖规则" prop="rewardRule">
          <el-input
            type="textarea"
            style="border: none; resize: none; width: 364px"
            v-model="groupForm.rewardRule"
            maxlength="100"></el-input>
        </el-form-item>
      </el-form>
    </div>
    <!-- <div class="g-card">
      <h4 class="g-card-title">新客欢迎语</h4>
      <el-form :model="groupForm" ref="welcomeTips" label-width="150px" >
        <div class="welcomeBox">
          <el-input type="textarea" style="border:none;resize:none" v-model="groupForm.welcomeMsg">
          </el-input>
          <div class="insertCustomer" @click="insertCustomer">
            <i class="el-icon-user-solid"></i>
            插入客户
          </div>
        </div>
      </el-form>
    </div> -->
    <el-button type="primary" @click="submitForm()">{{ pageType == 'edit' ? '立即修改' : '立即创建' }}</el-button>

    <!-- 选择发起员工弹窗 -->
    <SelectWeUser
      key="22222"
      :visible.sync="dialogVisibleSelectUser"
      title="选择成员"
      :isOnlyLeaf="false"
      :isSigleSelect="false"
      @success="selectedUser"></SelectWeUser>
    <!-- 选择标签弹窗 -->
    <SelectTag
      :visible.sync="dialogVisibleSelectTag"
      :selected="groupForm.toTag"
      @success="submitSelectTag"></SelectTag>
    <SelectPoster
      :visible.sync="dialogVisibleSelectPoster"
      :selected="groupForm.toTag"
      @success="submitSelecPoster"></SelectPoster>

    <!-- 选择添加员工弹窗 -->
    <SelectWeUser
      key="1"
      :visible.sync="dialogVisibleTargetStff"
      title="选择成员"
      :isOnlyLeaf="true"
      :isSigleSelect="true"
      @success="tagetSelect"></SelectWeUser>
  </div>
</template>

<style lang="scss">
.task-edit-container {
  .postersUrl {
    width: 70px;
    margin-right: 10px;
  }
  .changeUrl {
    color: var(--color);
    font-size: 14px;
    cursor: pointer;
  }
  .changePosterBody {
    display: flex;
    align-items: center;
  }
  .targetTips {
    font-size: 12px;
    color: #909399;
  }
  .welcomeBox {
    position: relative;
    width: 464px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    padding: 16px 16px 0 16px;
    margin-bottom: 10px;
    .el-textarea__inner {
      border: none;
      resize: none;
      height: 120px;
    }
    .insertCustomer {
      background-color: #ebeef5;
      border: none;
      color: #606266 !important;
      position: absolute;
      right: 55px;
      bottom: 10px;
      padding: 9px 15px;
      border-radius: 20px;
      cursor: pointer;
      font-size: 12px;
    }
  }
}
</style>
