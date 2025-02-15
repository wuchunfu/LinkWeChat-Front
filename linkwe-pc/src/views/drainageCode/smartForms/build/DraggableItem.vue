<script>
import draggable from 'vuedraggable'
import render from './generator/render'
const components = {
  itemBtns(h, element, index, parent) {
    const { copyItem, deleteItem } = this.$listeners
    return [
      <span
        class='drawing-item-copy ssc'
        title='复制'
        onClick={(event) => {
          copyItem(element, parent)
          event.stopPropagation()
        }}>
        <i class='el-icon-copy-document' />
      </span>,
      <span
        class='drawing-item-delete ssc'
        title='删除'
        onClick={(event) => {
          deleteItem(index, parent)
          event.stopPropagation()
        }}>
        <i class='el-icon-delete' />
      </span>,
    ]
  },
}
const layouts = {
  colFormItem(h, element, index, parent) {
    const { activeItem } = this.$listeners
    console.log('element.formId', element.formId)
    let className = this.activeId === element.formId ? 'drawing-item active-from-item' : 'drawing-item'
    //只是用顶部样式
    this.formConf.labelPosition = 'top'

    if (this.formConf.unFocusedComponentBorder) className += ' unfocus-bordered'
    return (
      <el-col
        span={element.span}
        class={className}
        nativeOnClick={(event) => {
          activeItem && activeItem(element)
          event.stopPropagation()
        }}>
        <el-form-item
          label-width={element.labelWidth ? `${element.labelWidth}px` : null}
          label={element.label}
          required={element.required}>
          <div class={element.captions ? 'elementCaptions' : ''}>{element.captions}</div>
          <div class={element.formCodeId == 4 ? 'flexC' : ''}>
            <div class={element.formCodeId == 4 ? 'right10 width200' : ''}>
              <div>{element.url}</div>
              <div
                // 无图片时，占位提示
                domPropsInnerHTML={
                  element.formCodeId == 1 && !element.src
                    ? '<div class="no-image">请在右侧控件设置中添加需要展示的图片</div>'
                    : ''
                }></div>
              <div class={element.formCodeId == '1' && !element.src ? 'nullC' : ''}>
                <render
                  key={element.renderKey}
                  conf={element}
                  onInput={(event) => {
                    this.$set(element, 'defaultValue', event)
                  }}
                />
              </div>
            </div>
            <div class={element.formCodeId == 4 ? 'elementInputNum' : 'nullC'}>
              <div class={element.unit ? '' : 'nullC'}>{element.unit}</div>
            </div>
          </div>
        </el-form-item>
        {components.itemBtns.apply(this, arguments)}
      </el-col>
    )
  },
  rowFormItem(h, element, index, parent) {
    const { activeItem } = this.$listeners
    const className = this.activeId === element.formId ? 'drawing-row-item active-from-item' : 'drawing-row-item'
    let child = renderChildren.apply(this, arguments)
    if (element.type === 'flex') {
      child = (
        <el-row type={element.type} justify={element.justify} align={element.align}>
          {child}
        </el-row>
      )
    }
    return (
      <el-col span={element.span}>
        <el-row
          gutter={element.gutter}
          class={className}
          nativeOnClick={(event) => {
            activeItem && activeItem(element)
            event.stopPropagation()
          }}>
          <span class='component-name'>{element.componentName}</span>
          <draggable list={element.children} animation={340} group='componentsGroup' class='drag-wrapper'>
            {child}
          </draggable>
          {components.itemBtns.apply(this, arguments)}
        </el-row>
      </el-col>
    )
  },
}

function renderChildren(h, element, index, parent) {
  console.log('1')
  if (!Array.isArray(element.children)) return null
  return element.children.map((el, i) => {
    const layout = layouts[el.layout]
    if (layout) {
      return layout.call(this, h, el, i, element.children)
    }
    return layoutIsNotFound()
  })
}

function layoutIsNotFound() {
  throw new Error(`没有与${this.element.layout}匹配的layout`)
}

export default {
  components: {
    render,
    draggable,
  },
  props: ['element', 'index', 'drawingList', 'activeId', 'formConf'],
  render(h) {
    const layout = layouts[this.element.layout]
    if (layout) {
      return layout.call(this, h, this.element, this.index, this.drawingList)
    }
    return layoutIsNotFound()
  },
}
</script>
<style lang="scss" scoped>
$selectedColor: #f6f7ff;

.drawing-item {
  position: relative;
  cursor: move;
  &.unfocus-bordered:not(.activeFromItem) > div:first-child {
    border: 1px dashed #ccc;
  }
  .el-form-item {
    padding: 12px 10px;
  }
}
.drawing-row-item {
  position: relative;
  cursor: move;
  box-sizing: border-box;
  border: 1px dashed #ccc;
  border-radius: 3px;
  padding: 0 2px;
  margin-bottom: 15px;
  .drawing-row-item {
    margin-bottom: 2px;
  }
  .el-col {
    margin-top: 22px;
  }
  .el-form-item {
    margin-bottom: 0;
  }
  .drag-wrapper {
    min-height: 80px;
  }
  &.active-from-item {
    border: 1px dashed var(--color);
  }
  .component-name {
    position: absolute;
    top: 0;
    left: 0;
    font-size: 12px;
    color: #bbb;
    display: inline-block;
    padding: 0 6px;
  }
}
.drawing-item,
.drawing-row-item {
  &:hover {
    & > .el-form-item {
      background: $selectedColor;
      border-radius: 6px;
    }
    & > .drawing-item-copy,
    & > .drawing-item-delete {
      display: initial;
    }
  }
  & > .drawing-item-copy,
  & > .drawing-item-delete {
    display: none;
    position: absolute;
    top: -10px;
    width: 22px;
    height: 22px;
    line-height: 22px;
    text-align: center;
    border-radius: 50%;
    font-size: 12px;
    border: 1px solid;
    cursor: pointer;
    z-index: 1;
  }
  & > .drawing-item-copy {
    right: 56px;
    border-color: var(--color);
    color: var(--color);
    background: #fff;
    &:hover {
      background: var(--color);
      color: #fff;
    }
  }
  & > .drawing-item-delete {
    right: 24px;
    border-color: #f56c6c;
    color: #f56c6c;
    background: #fff;
    &:hover {
      background: #f56c6c;
      color: #fff;
    }
  }
}
.active-from-item {
  & > .el-form-item {
    background: $selectedColor;
    border-radius: 6px;
  }
  & > .drawing-item-copy,
  & > .drawing-item-delete {
    display: initial;
  }
  & > .component-name {
    color: var(--color);
  }
}
::v-deep .no-image {
  color: #666;
  line-height: 80px;
}
.el-image {
  display: block;
}
.nullC {
  display: none;
}
.flexC {
  display: flex;
}
.width200 {
  width: 200px !important;
}
.right10 {
  margin-right: 10px;
}
</style>
