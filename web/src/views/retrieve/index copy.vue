<template>
  <div class="retrieve-container">
    <img src="@/assets/images/retrieve_bg.png" class="retrieve-bg" alt="">
    <img src="@/assets/images/specialist_bg.png" class="retrieve-bg-2" alt="">
    <!-- 搜索前的首页 -->
    <div v-if="!showSearchResults" class="home-page">
      <!-- 主标题区域 -->
      <div class="main-title">让工业知识会思考、会协作、会进化</div>

      <!-- 搜索框 -->
      <div class="search-section">
        <div style="display: flex;align-items: center;gap: 20px;">
          <div style="text-align: center;">
            <div>{{ summary.totalFiles }}</div>
            <div style="margin-top: 5px;">文件总数</div>
          </div>
          <div style="width: 1px;height: 40px; background: #979797;"></div>
          <div style="text-align: center;">
            <div>{{ summary.todayNewFiles }}</div>
            <div style="margin-top: 5px;">今日上新</div>
          </div>
          
        </div>
        <div class="search-input-wrapper">
          <input 
            ref="searchInputRef"
            v-model="queryParams.query"
            @input="handleSearchInput"
            @focus="showSearchDropdown = true"
            @blur="handleSearchBlur"
            type="text"
            @keyup.enter="performSearch"
            placeholder="请输入搜索"
            class="search-input"
          />
          <el-button @click="performSearch" class="search-button">
            <img src="@/assets/icons/svg/search-btn.svg" alt="">
          </el-button>
          
          <!-- 搜索下拉菜单 -->
          <div v-if="showSearchDropdown && searchSuggestions.length > 0" class="search-dropdown">
            <!-- <div class="dropdown-title">字体</div> -->
            <div 
              v-for="suggestion in searchSuggestions" 
              :key="suggestion.id"
   
              class="dropdown-item"
              @click="handleSelectSuggestion(suggestion.dictKey)"
            >
              {{ suggestion.dictKey }}
            </div>
          </div>
        </div>
        <div class="advanced-settings">
           <el-icon :size="18"><Setting /></el-icon>
          <span @click="openSettings">高级设置</span>
        </div>
      </div>

      <!-- 内容标签 -->
      <div class="content-tabs">
        <!-- <div class="tab-group">
          <div 
            v-for="(tab, index) in hotTabs" 
            :key="index"
            :class="['tab text-ellipsis', { active: activeHotTab === index }]"
            @click="activeHotTab = index"
          >
            {{ tab.content }}
          </div>
        </div>
        <div class="tab-separator"></div> -->
        <span style="line-height: 26px;">历史记录： </span>
        <div class="tab-group">
          <div 
            v-for="tab in historyTabs" 
            :key="tab"
            class="tab"
            @click="handleHistoryTab(tab)"
          >
          {{ tab.length > 8 ? tab.slice(0, 8) + '...' : tab }}
          </div>
        </div>
      </div>

      <!-- 知识列表 -->
      <div class="knowledge-section">
        <div class="knowledge-column">
          <div class="section-title">
            <span :class="{ active1: activek === 0 }" style="cursor: pointer;" @click="handleKnowledgeList(0)">最新知识</span>
            <span style="margin-left: 35px;cursor: pointer;" :class="{ active1: activek === 1 }"  @click="handleKnowledgeList(1)">最热知识</span>
          </div>
          <div v-if="latestKnowledge.length > 0" class="knowledge-list custom-scrollbar-container">
            <div v-for="(item, index) in latestKnowledge" :key="item.id" class="knowledge-item" @click="goDetail(item.fileId)">
              <img v-if="index === 0 && activek === 1" style="width: 21px;" :src="hot_1" alt="">
              <img v-else-if="index === 1 && activek === 1" style="width: 21px;" :src="hot_2" alt="">
              <img v-else-if="index === 2 && activek === 1" style="width: 21px;" :src="hot_3" alt="">
              <span v-else-if="activek === 1 " style="width: 21px; font-weight: 800; color: #D8D8D8;">{{ index < 9 ? '0' + (index + 1) : index + 1}}</span>
              <img v-else :src="getFilePath(item.fileName)" style="width: 21px;" alt="">
              <div class="knowledge-content">
                <div class="knowledge-title text-ellipsis" style="cursor: pointer;">{{ item.fileName }}</div>
                <div class="knowledge-meta">{{ item.knowledgeBaseName }}</div>
              </div>
              <div class="knowledge-time">{{ item.createdTimeText }}</div>
            </div>
          </div>
          <div  class="knowledge-list" v-else style="display: flex; align-items: center; justify-content: center; flex-direction: column;">
            <img :src="notData" style="width: 150px;" alt="">
            <span style="color:rgba(0, 0, 0, 0.6)">暂无知识</span>
          </div>
        </div>
        
        <div class="knowledge-column">
          <div class="section-title">
            <span>知识推荐</span>
          </div>
          <div class="knowledge-list custom-scrollbar" v-if="recommendedKnowledge.length > 0">
            <div v-for="item in recommendedKnowledge" :key="item.id" class="knowledge-item"  @click="goDetail(item.fileId)">
              <img :src="getFilePath(item.fileName)" style="width: 21px;" alt="">
              <div class="knowledge-content">
                <div class="knowledge-title text-ellipsis" style="cursor: pointer;">{{ item.fileName }}</div>
                <div class="knowledge-meta">{{ item.knowledgeBaseName }}</div>
              </div>
              <div class="knowledge-time">{{ item.createdTimeText }}</div>
            </div>
          </div>
          <div  class="knowledge-list" v-else style="display: flex; align-items: center; justify-content: center; flex-direction: column;">
             <img :src="notData" style="width: 150px;" alt="">
             <span style="color:rgba(0, 0, 0, 0.6)">暂无知识推荐</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索结果页面 -->
    <div v-if="showSearchResults" class="search-results-page">
      <!-- 搜索结果标题 -->
      <div class="search-results-header">
        <div style="display: flex;align-items: center;gap: 20px;">
          <div style="text-align: center;">
            <div>{{ summary.totalFiles }}</div>
            <div style="margin-top: 5px;">文件总数</div>
          </div>
          <div style="width: 1px;height: 40px; background: #979797;"></div>
          <div style="text-align: center;">
            <div>{{ summary.todayNewFiles }}</div>
            <div style="margin-top: 5px;">今日上新</div>
          </div>
          
        </div>
        <div class="search-input-wrapper">
          <input 
            v-model="queryParams.query" 
            @input="handleSearchInput"
            @focus="showSearchDropdown = true"
            @blur="handleSearchBlur"
            type="text" 
            @keyup.enter="performSearch"
            placeholder="搜索知识..."
            class="search-input"
          />
          <el-button @click="performSearch" class="search-button">
            <img src="@/assets/icons/svg/search-btn.svg" alt="">
          </el-button>
          
          <!-- 搜索下拉菜单 -->
          <div v-if="showSearchDropdown && searchSuggestions.length > 0" class="search-dropdown">
            <div 
              v-for="suggestion in searchSuggestions" 
              :key="suggestion.id"
              class="dropdown-item"
              @click="handleSelectSuggestion(suggestion.dictKey)"
            >
              {{ suggestion.dictKey }}
            </div>
          </div>
        </div>
        <div class="advanced-settings">
          <el-icon :size="18"><Setting /></el-icon>
          <span @click="openSettings">高级设置</span>
        </div>
      </div>

      <div style="background-color: #fff; padding: 20px 40px; flex: 1; display: flex; flex-direction: column; overflow: hidden;">
        <!-- 筛选条件 -->
      <div class="filters-section">
        <div class="filter-label">筛选设置：</div>
        <div class="filter-group">
          <div class="filter-item">
            <label>文件类型</label>
            <el-select v-model="filters.fileType" style="width: 150px;" clearable>
              <el-option value="docx" key="docx" label="docx"></el-option>
              <el-option value="doc" key="doc" label="doc"></el-option>
              <el-option value="pdf" key="pdf" label="pdf"></el-option>
              <el-option value="txt" key="txt" label="txt"></el-option>
              <el-option value="xlsx" key="xlsx" label="xlsx"></el-option>
              <el-option value="png" key="png" label="png"></el-option>
              <el-option value="jpg" key="jpg" label="jpg"></el-option>
              <el-option value="jpeg" key="jpeg" label="jpeg"></el-option>
              <el-option value="mp3" key="mp3" label="mp3"></el-option>
              <el-option value="mp4" key="mp4" label="mp4"></el-option>
              <el-option value="csv" key="csv" label="csv"></el-option>
              <el-option value="md" key="md" label="md"></el-option>
            </el-select>
          </div>
          <div class="filter-item">
            <label>知识空间</label>
            <!-- <el-select v-model="filters.knowledgeSpace" style="width: 150px;" clearable>
              
            </el-select> -->
            <el-cascader :options="options"  v-model="filters.knowledgeBaseIds" :props="defaultProps" :show-all-levels="false" clearable style="width: 150px;" />
          </div>
          <div class="filter-item">
            <label>更新时间</label>
            <el-select v-model="filters.updateTime" style="width: 150px;" clearable>
              <el-option value="ONE_MONTHS" label="一个月内" key="ONE_MONTHS"></el-option>
              <el-option value="THREE_MONTHS" label="三个月内" key="THREE_MONTHS"></el-option>
              <el-option value="HALF_YEAR" label="半年内" key="HALF_YEAR"></el-option>
              <el-option value="ONE_YEAR" label="一年内" key="ONE_YEAR"></el-option>
              <el-option value="ALL" label="不限时间" key="ALL"></el-option>
            </el-select>
          </div>
        </div>
      </div>

      <!-- 排序选项 -->
      <div class="sort-section">
        <div class="sort-label">排序方式：</div>
        <div class="sort-options">
          <span v-for="option in sortOptions" :key="option.value" @click="currentSort = option.value" :class="['sort-option', { active: currentSort === option.value }]"> 
            {{ option.label }}
            <span class="caret-wrapper" style="display: flex; flex-direction: column;">
              <i class="sort-caret ascending" 
                 :class="{ 'active-caret': sortDirection[option.value] === 'ascending' }"
                 @click.stop="handleSort(option.value,'ascending')"></i>
              <i class="sort-caret descending" 
                 :class="{ 'active-caret': sortDirection[option.value] === 'descending' }"
                 @click.stop="handleSort(option.value,'descending')"></i>
            </span>
          </span>
        </div>
      </div>

      <!-- 搜索结果列表 -->
      <div class="results-list" v-loading="loading">
        <div v-for="result in searchResults" :key="result.id" class="result-item" style="cursor: pointer;" @click="goDetail(result.document_id)">
          
          <div class="result-content">
            <div class="result-title" v-html="highlightKeyword(result.highlight)"></div>
            <!-- <div class="result-description">{{ result.description }}</div> -->
            <div class="result-meta">
              <div style="display: flex;align-items: center;gap: 50px;">
                <span style="display: flex;align-items: center;gap: 5px;"><img :src="getFilePath(result.filename)" style="width: 16px;" alt=""> {{ result.filename }}</span>
                <span v-for="itme in result.tags" :key="itme"><el-divider direction="vertical" />{{ itme }}</span>
                <!-- <span class="result-module"><el-divider direction="vertical" />{{ result.module }}</span> -->
              </div>
              <div style="display: flex;align-items: center;gap: 30px;">
                <span class="result-author">{{ result.createBy || '' }}</span>
                <span class="result-views"><el-icon><View /></el-icon>{{ result.viewCount || 0 }}</span>
                <!-- <span class="result-date">{{ moment(result.upload_time).format('YYYY-MM-DD') }}</span> -->
                <span class="result-relevance">相关度</span>
                <div class="result-rating">
                  <!-- <el-rate v-model="result.rating" disabled /> -->
                   <span>{{ result.score.toFixed(4) }}</span>
                </div>
              </div>
              
            </div>
          </div>
        </div>
        <div v-if="searchResults.length === 0 && !loading" style="display: flex;align-items: center;justify-content: center;height: 100%;">
          <el-empty
              description="暂无搜索结果"
              :image="notData"
            />
        </div>
      </div>
      
      <!-- 分页 -->
      <!-- <pagination  
      v-show="totalResults > 0"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      :total="totalResults"
      @pagination="getSearchResults" /> -->
      </div>
    </div>
    <el-dialog v-model="showSettings" title="高级设置" width="600" @close="cancel">
        <div class="segment-options">
          <el-radio-group v-model="form.searchType" class="segment-radio-group">
            <div class="radio-option">
              <el-radio value="hybrid" size="large">
                <div class="radio-content">
                  <div class="radio-title">混合检索</div>
                  <div class="radio-description">同时使用语义检索和全文检索进行查询，并对结果进行排序</div>
                </div>
              </el-radio>
            </div>
            <div class="radio-option">
              <el-radio value="semantic" size="large">
                <div class="radio-content">
                  <div class="radio-title">语义检索</div>
                  <div class="radio-description">基于向量化技术，通过分析查询的语义和上下文来提供更准确和相关的搜索结果</div>
                </div>
              </el-radio>
            </div>
            
            <div class="radio-option">
              <el-radio value="fulltext" size="large">
                <div class="radio-content">
                  <div class="radio-title">全文检索</div>
                  <div class="radio-description">通过关键词精准匹配进行检索</div>
                </div>
              </el-radio>
            </div>
            </el-radio-group>   
        </div>
        <div class="recall-title">
            <span  style="min-width: 120px; display: flex;margin-right: 10px;">
              最大召回分段
              <el-tooltip content="最大召回字数内，知识库查询结果中可以被召回的最大段落数" placement="top">
                <el-icon style="color:#D8D8D8; font-size: 12px;"><question-filled /></el-icon>
              </el-tooltip>
            </span>
            <el-slider style="padding-left: 5px" v-model="form.maxSegmentCount" :max="20" :min="1" size="small" :marks="marks" class="max-recall-slider" />
            <span style="background: #F2F4FF; padding: 5px 10px; margin-left: 20px;">{{  form.maxSegmentCount }}</span>
        </div>
        <div class="recall-title">
            <span  style="min-width: 120px; display: flex;margin-right: 10px;">
              结果重排
              <el-tooltip content="使用重拍模型对召回结果进行二次排序，增强召回效果" placement="top" >
                <el-icon style="color:#D8D8D8; font-size: 12px;"><question-filled /></el-icon>
              </el-tooltip>
            </span>
            <el-select v-model="form.rerankModel" style="flex: 1;" size="large" class="rerank-select" clearable @visible-change="handleSelectVisible">
              <el-option v-for="item in rerank_model" :key="item.value" :label="item.label" :value="item.value">
                <div>{{ item.label }}</div>
                <div
                  style="color: var(--el-text-color-secondary);
                  font-size: 13px;"
                >
                  {{ item.remark }}
                </div>
              </el-option>
            </el-select>
        </div>
        <template #footer>
            <el-button  @click="cancel">取消</el-button>
            <el-button type="primary" @click="confirm">确定</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { 
  getKnowledgeRetrieveSummary, 
  getKnowledgeRetrieveRecommend, 
  getKnowledgeRetrieveLatest,
  getKnowledgeRetrieveSearch
} from '@/api/retrieve'
import { hitTestQuery, queryAllKnowledgeBase, knowledgeSpaceList } from '@/api/base/'
import { filterEmptyValues } from '@/utils'
import notData from '@/assets/images/notData.png'
import moment from 'moment'
import hot_1 from '@/assets/images/hot_1.svg'
import hot_2 from '@/assets/images/hot_2.svg'
import hot_3 from '@/assets/images/hot_3.svg'

const { proxy } = getCurrentInstance()
const router = useRouter()
const route = useRoute()
const { rerank_model } = toRefs(proxy?.useDict('rerank_model'));
const searchInputRef = ref(null)

// 响应式数据
const currentSearchKeyword = ref('')
const showSearchResults = ref(false)
const showSearchDropdown = ref(false)
const activeHotTab = ref(0)
const activeHistoryTab = ref(0)
const currentSort = ref('relevance')
const queryParams = ref({
  query: '',
  rerankModel: '',
  source: 'vector_search',
  maxSegmentCount: 10,
  searchType: 'fulltext'
})
const placeholder = ref('搜索知识...')
const currentPlaceholderIndex = ref(0)
const placeholderTimer = ref(null)

const totalResults = ref(500)
const activek = ref(0)

const marks = {
  0: '0',
  20: '20'
}
const marks1 = {
  0: '0',
  3000: '3000'
}
const handleKnowledgeList = (index) => {
  activek.value = index
  if (index === 0) {
    latestKnowledge.value = latest.value.latestKnowledge.slice(0, 10)
  } else {
    latestKnowledge.value = latest.value.hotKnowledge.slice(0, 10)
  }
}

const options = ref([])

const defaultProps = {
  lazy: true,
  lazyLoad(node, resolve) {
    const { level } = node
    if (level === 0) {
      resolve(options.value)
    } else if (level === 1) {
        console.log('node', node)
        knowledgeSpaceList(node.data.value).then(res => {
          node.data.children = res.data.map(item => ({
            value: item.id,
            label: item.name,
            leaf: true
          }))
          resolve(node.data.children)
        })
    }
  },
}

const summary = ref({})
const recommend = ref({})
const latest = ref({})

const showSettings = ref(false)
const form = ref({  // 高级设置
    maxSegmentCount: 10,
    searchType: 'fulltext',
    rerankModel: 'qwen3-rerank',
    // maxCount: 0
})



// 搜索建议
const searchSuggestions = ref([])

// 筛选条件
const filters = ref({
  fileType: null,
  knowledgeBaseIds: [],
  knowledgeId: [],
  updateTime: null
})

// 标签数据
const hotTabs = ref([])
const historyTabs = ref([])

// 排序选项
const sortOptions = [
    { label: '关联程度', value: 'relevance' },
    { label: '访问次数', value: 'views' },
    // { label: '收藏次数', value: 'favorites' },
    // { label: '评论次数', value: 'comments' },
    { label: '更新时间', value: 'updateTime' }
]

const loading = ref(false)


// 知识列表数据
const latestKnowledge = ref([])

const hotKnowledge = ref([])

const recommendedKnowledge = ref([])

// 搜索结果数据
const searchResults = ref([])
const getSearchResults = () => {
  // 处理搜索结果
}
// 计算属性
const totalPages = computed(() => Math.ceil(totalResults.value / pageSize.value))

const visiblePages = computed(() => {
  const pages = []
  const start = Math.max(1, currentPage.value - 2)
  const end = Math.min(totalPages.value, currentPage.value + 2)
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  
  return pages
})

const showEllipsis = computed(() => {
  return totalPages.value > 10 && currentPage.value < totalPages.value - 3
})

const handleHistoryTab = (tab) => {
  queryParams.value.query = tab
  performSearch()
}

// 防抖定时器
let searchTimer = null

// 方法
const handleSearchInput = () => {
  showSearchDropdown.value = queryParams.value.query.length > 0
  
  // 清除之前的定时器
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
  
  // 如果没有输入内容，直接返回
  if (!queryParams.value.query.trim()) {
    showSearchResults.value = false
    setTimeout(() => {
      searchInputRef.value.focus()
    })
    return
  }
  // 设置防抖延迟
  searchTimer = setTimeout(() => {
    getKnowledgeRetrieveSearch({
      dictType: 'SUGGESTION',
      dictKey: queryParams.value.query
    }).then(res => {
      // 更新搜索建议列表
      if (res.data && Array.isArray(res.data)) {
        searchSuggestions.value = res.data
      }
    })
  }, 300) // 300ms 防抖延迟
}

const sortDirection = ref({
  relevance: '',
  views: '',
  favorites: '',
  comments: '',
  updateTime: ''
})

const handleSort = (value, type) => {
  // 更新排序方向
  sortDirection.value[value] = type

  switch (value) {
    case 'relevance':
      if (type === 'ascending') {
        searchResults.value.sort((a, b) => a.score - b.score)
      } else {
        searchResults.value.sort((a, b) => b.score - a.score)
      }
      break;
    case 'views':
      if (type === 'ascending') {
        searchResults.value.sort((a, b) => a.viewCount - b.viewCount)
      } else {
        searchResults.value.sort((a, b) => b.viewCount - a.viewCount)
      }
      break;
    case 'favorites':
      break;  
    case 'comments':
      break;
    case 'updateTime':
      break;
  }
}

const handleSelectSuggestion = (suggestion) => {
  queryParams.value.query = suggestion
  showSearchDropdown.value = false
  performSearch()
}


const cancel = () => {
  showSettings.value = false
}

const confirm = () => {
  showSettings.value = false
  queryParams.value = {
    ... queryParams.value,
    ... form.value
  }
  // performSearch()
}

const handleSelectVisible = (visible) => {
  if (visible) {
    // 下拉菜单展开时应用样式
    nextTick(() => {
      setTimeout(() => {
        const dropdownItems = document.querySelectorAll('.el-select-dropdown__item')
        dropdownItems.forEach(item => {
          // 检查是否包含两个 div 子元素（label 和 remark）
          const divs = item.querySelectorAll('div')
          if (divs.length >= 2) {
            item.style.height = '70px'
            item.style.lineHeight = '1.4'
            item.style.padding = '8px 20px'
            item.style.whiteSpace = 'normal'
            item.style.display = 'flex'
            item.style.flexDirection = 'column'
            item.style.justifyContent = 'center'
          }
        })
      }, 50)
    })
  }
}

const handleSearchBlur = () => {
  setTimeout(() => {
    showSearchDropdown.value = false
  }, 200)
}

const openSettings = () => {
  showSettings.value = true
  form.value = {
    ...form.value,
    ... queryParams.value
  }
  console.log('form.value', form.value)
  
  // 等下拉菜单渲染后再应用样式
  nextTick(() => {
    setTimeout(() => {
      const dropdownItems = document.querySelectorAll('.el-select-dropdown__item')
      dropdownItems.forEach(item => {
        // 检查是否包含两个 div 子元素（label 和 remark）
        const divs = item.querySelectorAll('div')
        if (divs.length >= 2) {
          item.style.height = '70px'
          item.style.lineHeight = '1.4'
          item.style.padding = '8px 20px'
          item.style.whiteSpace = 'normal'
          item.style.display = 'flex'
          item.style.flexDirection = 'column'
          item.style.justifyContent = 'center'
        }
      })
    }, 100)
  })
}


const selectSuggestion = (suggestion) => {
  queryParams.value.query = suggestion
  showSearchDropdown.value = false
  performSearch()
}

const performSearch = () => {
  if ( queryParams.value.query === '' ) {
    return
  }
    currentSearchKeyword.value = queryParams.value.query
    showSearchResults.value = true
    showSearchDropdown.value = false
    loading.value = true
    
    // 过滤后的参数
    const filteredParams = filterEmptyValues(queryParams.value)
    
    hitTestQuery(filteredParams).then(res => {
      searchResults.value = res.data
      // searchResults.value.forEach(item => {
      //   if (item.score > 1 && item.score <= 10) {
      //       item.rating = 1
      //   } else if (item.score > 10 && item.score <= 20) {
      //       item.rating = 2
      //   } else if (item.score > 20 && item.score <= 40) {
      //       item.rating = 3
      //   } else if (item.score > 40 && item.score <= 80) {
      //       item.rating = 4
      //   } else {
      //       item.rating = 5
      //   }
      // });
      loading.value = false
    })
}

  const getSortIcon = (sortValue) => {
  return currentSort.value === sortValue ? '▲' : '▼'
}

const getFilePath = (fileName) => {
  if (!fileName) return new URL('../../assets/fileIcon/othe.svg', import.meta.url).href;
  
  // 获取文件扩展名
  const extension = fileName.toLowerCase().split('.').pop();
  
  // 定义支持的文件类型
  const supportedExtensions = ['jpg', 'html', 'doc', 'ppt', 'mp4', 'txt', 'pdf', 'xls', 'png', 'md', 'csv', 'xlsx', 'xlsx', 'docx', 'jpeg'];
  
  // 特殊处理一些扩展名
  let iconName = extension;
  // 检查是否有对应的图标
  if (supportedExtensions.includes(iconName)) {
    return new URL(`../../assets/fileIcon/${iconName}.svg`, import.meta.url).href;
  } else {
    return new URL('../../assets/fileIcon/othe.svg', import.meta.url).href;
  }
}

const goDetail = (id) => {
  // 保存当前搜索状态到路由query中
  const currentState = {
    id: id,
    // 保存搜索状态
    fromSearch: showSearchResults.value,
    searchQuery: queryParams.value.query,
    searchType: queryParams.value.searchType,
    maxSegmentCount: queryParams.value.maxSegmentCount,
    rerankModel: queryParams.value.rerankModel,
    source: queryParams.value.source,
    // 保存筛选状态
    fileType: filters.value.fileType,
    knowledgeBaseIds: JSON.stringify(filters.value.knowledgeBaseIds),
    knowledgeId: JSON.stringify(filters.value.knowledgeId),
    updateTime: filters.value.updateTime,
    // 保存排序状态
    currentSort: currentSort.value,
    sortDirection: JSON.stringify(sortDirection.value)
  }
  
  router.push({
    path: '/space/retrieve/detail',
    query: currentState
  })
}

let getList = async () => {
  const res = await getKnowledgeRetrieveSummary()
  summary.value = res.data
  historyTabs.value = res.data.historyList.length > 3 ? res.data.historyList.slice(0, 3) : res.data.historyList
  hotTabs.value =  res.data.hotSearchList
  
  // 设置初始placeholder并启动轮播
  if (hotTabs.value.length > 0) {
    placeholder.value = hotTabs.value[0].content || '搜索知识...';
    currentPlaceholderIndex.value = 0;
    startPlaceholderCarousel();
  }
  
  const resRecommend = await getKnowledgeRetrieveRecommend()
  recommendedKnowledge.value = resRecommend.data.recommendKnowledge.slice(0, 10)
  const resLatest = await getKnowledgeRetrieveLatest()
  latest.value = resLatest.data
  latestKnowledge.value = latest.value.latestKnowledge.slice(0, 10)
}

watch(filters.value, (newVal) => {
  console.log('newVal', newVal)
  queryParams.value = {
    ... queryParams.value,
    ... newVal,
    knowledgeBaseIds: newVal.knowledgeBaseIds != undefined && newVal.knowledgeBaseIds.length > 0 ? [newVal.knowledgeBaseIds[newVal.knowledgeBaseIds.length - 1]] : [],
    knowledgeId: newVal.knowledgeBaseIds != undefined && newVal.knowledgeBaseIds.length > 0 ? [newVal.knowledgeBaseIds[0]] : [] 
  }
  console.log('queryParams.value', queryParams.value)
  performSearch()
}, { deep: true })
// 恢复搜索状态的函数
const restoreSearchState = () => {
  
  // 检查是否从详情页返回（路由中包含搜索状态参数）
  if (route.query.fromSearch === 'true') {
    console.log('从详情页返回，恢复搜索状态...', route.query)
    
    // 恢复搜索参数
    queryParams.value = {
      query: route.query.searchQuery || '',
      searchType: route.query.searchType || 'fulltext',
      maxSegmentCount: parseInt(route.query.maxSegmentCount) || 10,
      rerankModel: route.query.rerankModel || ''
    }
    
    console.log('恢复的搜索参数:', queryParams.value)
    
    // 恢复筛选状态
    filters.value = {
      fileType: route.query.fileType || null,
      knowledgeBaseIds: route.query.knowledgeBaseIds ? JSON.parse(route.query.knowledgeBaseIds) : [],
      knowledgeId: route.query.knowledgeId ? JSON.parse(route.query.knowledgeId) : [],
      updateTime: route.query.updateTime || null
    }
    
    // 恢复排序状态
    currentSort.value = route.query.currentSort || 'relevance'
    if (route.query.sortDirection) {
      sortDirection.value = JSON.parse(route.query.sortDirection)
    }
    
    // 恢复搜索结果显示状态
    showSearchResults.value = true
    currentSearchKeyword.value = queryParams.value.query
    
    // 重新执行搜索以获取结果
    if (queryParams.value.query) {
      performSearch()
    }
    
    // 清理路由参数，避免影响后续操作
    router.replace({ path: '/space/retrieve', query: {} })
  }
}

// 生命周期
onMounted(() => {
  // 初始化逻辑
  getList()
  queryAllKnowledgeBase().then(res => {
      options.value = res.data.map(item => ({
        value: item.id,
        label: item.name,
        leaf: false,
        children: []
      }))
      console.log('options.value', options.value)
  })
  
  // 检查并恢复搜索状态
  restoreSearchState()
})

// 组件销毁时清理定时器
onBeforeUnmount(() => {
  if (searchTimer) {
    clearTimeout(searchTimer)
    searchTimer = null
  }
  stopPlaceholderCarousel()
})

const highlightKeyword = (text) => {
  if (!currentSearchKeyword.value || !text) return text;
  
  let result = text;
  // 第一步：将 <em> 标签替换为 <mark> 标签
  result = result
    .replace(/<em>/g, '<mark>')
    .replace(/<\/em>/g, '</mark>');
  
  // 第二步：将 <mark> 标签替换为带样式的 <span> 标签
  result = result
    .replace(/<mark>/g, '<span style="color: #447DFB;">')
    .replace(/<\/mark>/g, '</span>');
  
  // 第三步：处理用户搜索关键词的高亮
  const escapedKeyword = currentSearchKeyword.value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  const regex = new RegExp(`(${escapedKeyword})`, 'gi');
  result = result.replace(regex, '<span style="color: #447DFB;">$1</span>');
  return result;
}

// Placeholder轮播功能
const startPlaceholderCarousel = () => {
  if (!hotTabs.value.length) return;
  
  placeholderTimer.value = setInterval(() => {
    if (hotTabs.value.length > 0) {
      currentPlaceholderIndex.value = (currentPlaceholderIndex.value + 1) % hotTabs.value.length;
      placeholder.value = hotTabs.value[currentPlaceholderIndex.value].content || '搜索知识...';
    }
  }, 5000); // 5秒切换一次
}

const stopPlaceholderCarousel = () => {
  if (placeholderTimer.value) {
    clearInterval(placeholderTimer.value);
    placeholderTimer.value = null;
  }
}
</script>

<style scoped lang="scss">
.retrieve-container {
  height: 100%;
  background: linear-gradient(#ECEFFD 100%, #F2F3F8 100%);
  position: relative;
  .retrieve-bg {
    position: absolute;
    transform: translate(-50%, 0);
    left: 50%;
    top: 0;
  }
  .home-page {
    position: relative;
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    .main-title {
      padding-top: 70px;
      width: 450px;
      margin: 0 auto;
      font-weight: bold;
      font-size: 28px;
      background: linear-gradient(to right, #5C078F, #AF50C5 );
      -webkit-background-clip: text;
      background-clip: text;
      -webkit-text-fill-color: transparent;
      color: transparent;
      text-align: center;
    }
    
    .search-section {
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 20px;
      margin: 20px 0 10px 0;
      
      .advanced-settings {
        width: 170px;
        display: flex;
        align-items: center;
        gap: 5px;
        cursor: pointer;
      }
    }
    
    .content-tabs {
      display: flex;
      padding-left: 20px;
      gap: 10px;
      width: 760px;
      height: 26px;
      margin: 0 auto;
      color: rgba(0,0,0,0.6);
      .tab-group {
        display: flex;
        gap: 10px;
      }
      
      .tab {
        padding: 0 10px;
        line-height: 26px;
        background: rgba(255,255,255,0.58);
        cursor: pointer;
        color: rgba(0,0,0,0.6);
        &:hover {
          color: #6B05A8;
        }
      }
      
      .tab-separator {
        // width: 1px;
        padding: 0 1px;
        background: #000000;
        margin: 0 20px;
      }
    }
    
    .knowledge-section {
      display: flex;
      justify-content: center;
      gap: 20px;
      margin: 20px auto;
      flex: 1;
      overflow: hidden;
      .knowledge-column {
        
        
        .section-title {
          font-size: 16px;
          margin-bottom: 10px;
        }
        
        .knowledge-list {
          padding: 10px 20px;
          border-radius: 8px;
          background: #fff;
          width: 550px;
          height: calc(100% - 35px);
          max-height: 500px;
          overflow-y: auto;
          .knowledge-item {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 10px 0;
            border-bottom: 1px solid #eee;
            cursor: pointer;
            &:last-child {
              border-bottom: none;
            }
            &:hover {
              .knowledge-title {
                color: #6B05A8 !important;
            }
            
            }
            .knowledge-content {
              flex: 1;
              overflow: hidden;
              .knowledge-title {
                font-size: 0.9rem;
                color: #333;
                margin-bottom: 5px;
              }
              
              .knowledge-meta {
                font-size: 0.8rem;
                color: #666;
              }
            }
            
            .knowledge-time {
              font-size: 0.8rem;
              color: #999;
            }
          }
        }
      }
    }
  }
  
  .search-results-page {
    height: 100%;
    position: relative;
    display: flex;
    flex-direction: column;
    // padding: 20px;
    
    .search-results-header {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 20px;
      padding: 20px 0;
      border-bottom: 1px solid #eee;
      
      .search-results-title {
        font-size: 1.5rem;
        color: #333;
      }
         
      .advanced-settings {
        width: 170px;
        display: flex;
        align-items: center;
        gap: 5px;
        cursor: pointer;
      }
    }
    
    .filters-section {
      display: flex;
      align-items: center;
      gap: 20px;   
      .filter-label {
        font-weight: bold;
        color: #333;
      }
      
      .filter-group {
        display: flex;
        gap: 15px;
        
        .filter-item {
          display: flex;
          align-items: center;
          gap: 5px;
          
          label {
            font-size: 0.9rem;
            color: #666;
          }
          
          select {
            padding: 5px 10px;
            border: 1px solid #ddd;
            border-radius: 3px;
            
            &:focus {
              outline: none;
              border-color: #7b68ee;
            }
          }
        }
      }
    }
    
    .sort-section {
      display: flex;
      align-items: center;
      gap: 20px;
      padding: 20px 0;
      
      .sort-label {
        font-weight: bold;
        color: #333;
      }
      
      .sort-options {
        display: flex;
        gap: 10px;
        
        .sort-option {
          display: flex;
          align-items: center;
          cursor: pointer;
          font-size: 14px;
        }
      }
    }
    
    .results-list {
      flex: 1;
      overflow-y: auto;
      .result-item {
        display: flex;
        padding: 10px 20px 0 20px;
        background: #f9f9f9;
        margin-bottom: 20px;       
        .result-icon {
          font-size: 14px;
          color: #7b68ee;
        }
        
        .result-content {
          flex: 1;
          
          .result-title {
            font-size: 16px;
            color: #333;
            margin-bottom: 10px;
            line-height: 1.4;
          }
          
          .result-description {
            color: #666;
            line-height: 1.5;
            margin-bottom: 10px;
          }
          
          .result-meta {
            display: flex;
            align-items: center;
            justify-content: space-between;
            font-size: 14px;
            color: #999;
            
            .result-category,
            .result-module,
            .result-author,
            .result-views,
            .result-date,
            .result-relevance {
              display: flex;
              align-items: center;
              gap: 5px;
            }
            
            .result-rating {
              display: flex;
              gap: 2px;
              
              .star {
                font-size: 0.8rem;
              }
            }
          }
        }
      }
    }
    
    .pagination {
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 10px;
      padding: 30px 0;
      
      .pagination-btn {
        padding: 8px 12px;
        border: 1px solid #ddd;
        background: white;
        border-radius: 3px;
        cursor: pointer;
        
        &:hover {
          background: #f5f5f5;
        }
        
        &.active {
          background: #7b68ee;
          color: white;
          border-color: #7b68ee;
        }
        
        &:disabled {
          opacity: 0.5;
          cursor: not-allowed;
        }
      }
      
      .pagination-ellipsis {
        padding: 8px 12px;
        color: #999;
      }
      
      .pagination-info {
        margin-left: 20px;
        display: flex;
        align-items: center;
        gap: 10px;
        
        .page-size-select {
          padding: 5px 8px;
          border: 1px solid #ddd;
          border-radius: 3px;
        }
      }
    }
  }
}
.recall-title {
    font-size: 16px;
    // font-weight: 600;
    margin-bottom: 20px;
    color: #333;
    display: flex;
    align-items: center;
}
.search-input-wrapper {
        position: relative;
        width: 760px;
        
        .search-input {
          width: 100%;
          padding: 0 20px;
          border: none;
          font-size: 1rem;
          height: 66px;
          border-radius: 8px;
          box-shadow: 0px 4px 8px 0px rgba(199,207,223,0.3);
          &:focus {
            outline: none;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
          }
        }
        
        .search-button {
          position: absolute;
          right: 10px;
          top: 50%;
          transform: translateY(-50%);
          background: none;
          border: none;
          font-size: 1.2rem;
          cursor: pointer;
        }
        
        .search-dropdown {
          position: absolute;
          top: 100%;
          left: 0;
          right: 0;
          background: white;
          border-radius: 8px;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          z-index: 1000;
          margin-top: 5px;
          
          .dropdown-title {
            padding: 10px 15px;
            font-weight: bold;
            border-bottom: 1px solid #eee;
          }
          
          .dropdown-item {
            padding: 10px 15px;
            cursor: pointer;
            
            &:hover {
              background: #f5f5f5;
            }
          }
        }
      }
  .caret-wrapper{
    display: inline-flex;
    flex-direction: column;
    align-items: center;
    height: 14px;
    width: 24px;
    vertical-align: middle;
    cursor: pointer;
    position: relative;
    overflow: initial;
    .sort-caret {
      width: 0px;
      height: 0px;
      position: absolute;
      left: 7px;
      border-width: 5px;
      border-style: solid;
      border-color: transparent;
      border-image: initial;
      transition: all 0.3s;
    }
    .sort-caret.ascending {
      top: -5px;
      border-bottom-color: #a8abb2;
      
      &.active-caret {
        border-bottom-color: #409eff;
      }
    }
    .sort-caret.descending {
      bottom: -3px;
      border-top-color: #a8abb2;
      
      &.active-caret {
        border-top-color: #409eff;
      }
    }
  } 
  .active1 {
    color: #6B05A8;
    font-weight: 600;
  }
  
  .result-title .highlight,
  .highlight {
    color: #447DFB;
    display: inline !important;
  }
  
  :deep(.highlight) {
    color: #447DFB;
  }
  :deep(.el-input input::placeholder) {
    color: rgba(0,0,0,0.4) !important;
  }
  :deep(.el-slider__button) {
    width: 15px;
    height: 15px;
  }
  .segment-options {
       margin-bottom: 10px;
      .segment-radio-group {
        display: flex;
        flex-direction: column;
        gap: 10px;
       
        .radio-option {
          padding: 10px;
          transition: all 0.3s;
          background: #F2F6FF;
          width: 100%; 
          :deep(.el-radio) {
            width: 100%;
            
            .el-radio__label {
              width: 100%;
              padding-left: 20px;
            }
          }
          
          .radio-content {
            .radio-title {
              font-size: 16px;
              color: #303133;
              margin-bottom: 8px;
            }
            
            .radio-description {
              font-size: 14px;
              color: #909399;
            }
          }
        }
      }
    }
    .retrieve-bg-2 {
        position: absolute;
        right: 0;
        top: 0;
        height: 35%;
  }
</style> 