<template>
  <div class="modal">
    <el-dialog title="选择客群" :visible.sync="modalVisible" :close-on-click-modal="false">
      <div class="fixed-content">
        已选择：
        <template v-for="(item, index) in groupChildList">
          <el-tag
            style="margin-bottom: 5px"
            v-if="item.groupName"
            size="medium"
            :key="index"
            closable
            @close="handleClose(item)">
            {{ item.groupName }}
          </el-tag>
        </template>
      </div>
      <el-form
        :model="query"
        ref="queryForm"
        :inline="true"
        class="top-search"
        label-width="40px"
        label-position="left">
        <el-form-item label="" prop="groupName">
          <el-input style="width: 150px" v-model="query.groupName" placeholder="请输入群名" clearable />
        </el-form-item>
        <el-form-item label="" prop="groupLeaderName">
          <el-input style="width: 150px" v-model="query.groupLeaderName" placeholder="请输入群主" clearable />
        </el-form-item>
        <el-form-item label="" prop="">
          <el-input
            style="width: 150px"
            :value="tagNames"
            readonly
            @focus="showSelectTag = true"
            placeholder="请选择客群标签" />
        </el-form-item>
        <el-form-item label="">
          <el-button type="primary" @click="getList(1)">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table
        ref="multipleTable"
        v-loading="loading"
        :data="list"
        @select="selectFn"
        @select-all="selectAllFn"
        @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="群名" align="center" prop="groupName" />
        <el-table-column label="群主" align="center" prop="groupLeaderName" />
        <el-table-column label="群标签" width="180">
          <template #default="{ row }">
            <div v-if="row.tags">
              <tag-ellipsis :list="row.tags.split(',')" limit="1"></tag-ellipsis>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="群总人数 " align="center " prop="memberNum" />
      </el-table>
      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="query.pageNum"
        :limit.sync="query.pageSize"
        @pagination="getList()" />
      <div slot="footer">
        <el-button @click="cancelFn">取 消</el-button>
        <el-button type="primary" @click="submit">确 定</el-button>
      </div>
    </el-dialog>
    <select-tag :visible.sync="showSelectTag" type="2" :selected="tagList" @success="getSelectTag"></select-tag>
  </div>
</template>
<script>
import * as api from '@/api/customer/group'
import SelectTag from '@/components/SelectTag.vue'

export default {
  name: 'select-group',
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    limit: {
      type: Number,
      default: 5,
    },
    defaults: {
      type: Array,
      default: () => [],
    },
  },
  components: { SelectTag },
  data() {
    return {
      query: {
        pageNum: 1,
        pageSize: 10,
        groupName: '',
        groupLeaderName: '',
        tagIds: '',
      },
      total: 0,
      list: [],
      loading: false,
      groupChildList: [],
      tagNames: '',
      showSelectTag: false,
      tagList: [],
    }
  },
  computed: {
    modalVisible: {
      get() {
        return this.visible
      },
      set(val) {
        this.$emit('update:visible', val)
      },
    },
  },
  watch: {
    visible(val) {
      val && this.getList(1)
      this.groupChildList = JSON.parse(JSON.stringify(this.defaults))
    },
  },
  methods: {
    getSelectTag(list) {
      this.taglist = list
      let arr = []
      let arr1 = []
      list.forEach((dd) => {
        arr.push(dd.name)
        arr1.push(dd.tagId)
      })
      this.tagNames = arr.join(',')
      this.query.tagIds = arr1.join(',')
    },
    handleClose(tag) {
      this.groupChildList.splice(this.groupChildList.indexOf(tag), 1)
      this.list.forEach((row) => {
        if (row.chatId === tag.chatId) {
          this.$refs.multipleTable.toggleRowSelection(row, false)
        }
      })
    },
    resetQuery() {
      ;(this.query = {
        pageNum: 1,
        pageSize: 10,
        groupName: '',
        groupLeaderName: '',
        tagIds: '',
      }),
        (this.tagList = [])
      this.tagNames = ''
      this.getList()
    },
    getList(page) {
      this.loading = true
      page && (this.query.pageNum = page)
      api.getList(this.query).then((res) => {
        this.total = Number(res.total)
        this.list = res.rows
        this.loading = false
        setTimeout(() => {
          this.toggleSelection()
        }, 300)
      })
    },
    toggleSelection() {
      if (this.groupChildList) {
        this.groupChildList.forEach((row) => {
          this.list.forEach((data) => {
            if (row.chatId === data.chatId) {
              this.$refs.multipleTable.toggleRowSelection(data)
            }
          })
        })
      } else {
        this.$refs.multipleTable.clearSelection()
      }
    },
    handleSelectionChange(selection) {
      let arr = selection
      this.groupChildList.forEach((val) => {
        arr = arr.filter((item) => {
          return item.chatId !== val.chatId
        })
      })
      this.groupChildList.push(...arr)
    },
    selectFn(data, row) {
      if (!data.includes(row)) {
        let index = this.groupChildList.findIndex((arr) => arr.chatId === row.chatId)
        if (index !== -1) {
          this.groupChildList.splice(index, 1)
        }
      }
    },
    selectAllFn(data) {
      console.log(data)
      if (!data.length) {
        this.list.forEach((dd) => {
          let index = this.groupChildList.findIndex((arr) => arr.chatId === dd.chatId)
          if (index !== -1) {
            this.groupChildList.splice(index, 1)
          }
        })
      }
    },
    submit() {
      if (!this.groupChildList.length) {
        this.msgInfo('请至少选择一个客群！')
        return
      }
      if (this.groupChildList.length > 5) {
        this.msgInfo('最多选择五个客群！')
        return
      }
      this.$emit('submit', this.groupChildList)
      this.list = []
      this.modalVisible = false
    },
    cancelFn() {
      this.list = []
      this.modalVisible = false
    },
  },
  created() {},
}
</script>
<style lang="scss" scoped>
::v-deep .el-dialog__body {
  margin: 30px 20px;
  padding: 0;
  color: #606266;
  font-size: 14px;
  word-break: break-all;
}
.fixed-content {
  position: sticky;
  top: 0;
  left: 0;
  z-index: 10;
  background-color: #fff;
}
</style>
