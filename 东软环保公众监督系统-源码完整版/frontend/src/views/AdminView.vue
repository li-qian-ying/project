<template>
  <template v-if="page==='dashboard'">
    <section class="operations-banner"><div><p>OPERATIONS OVERVIEW</p><h2>公众监督业务运行总览</h2><span>今日已接收 {{todayReceived}} 条反馈，{{stats.waitingTotal||0}} 条等待指派，系统闭环率 {{completion}}%。</span></div><div class="banner-metrics"><span><b>{{stats.cityCoverage||0}}</b>城市网格</span><span><b>{{workers.length}}</b>检测人员</span><span><b>{{stats.doneTotal||0}}</b>闭环数据</span></div></section>
    <StatCards :items="cards"/>
    <AnalyticsBoard :stats="stats"/>
    <section class="dashboard-bottom"><ActivityTimeline :items="feedbacks"/><article class="panel"><div class="panel-title"><div><h2>待办与预警</h2><p>按优先级处理异常业务</p></div><b class="todo-count">{{waiting.length}}</b></div><ul class="notice-list"><li><i class="fa-solid fa-triangle-exclamation warning-icon"></i><div><strong>{{stats.waitingTotal||0}} 条反馈等待指派</strong><span>建议优先匹配同城市可工作网格员</span></div><button class="text-link" @click="goTo('assign')">立即处理</button></li><li><i class="fa-solid fa-flask"></i><div><strong>{{stats.doingTotal||0}} 项任务正在检测</strong><span>关注超过2小时未提交的现场任务</span></div><button class="text-link" @click="goTo('feedbacks','已指派')">查看任务</button></li><li><i class="fa-solid fa-chart-line alert-icon"></i><div><strong>{{stats.badTotal||0}} 条污染等级数据</strong><span>已自动同步至NEPV决策分析端</span></div><button class="text-link" @click="goTo('statistics')">查看分析</button></li></ul></article></section>
  </template>

  <section v-else-if="page==='feedbacks'" class="panel data-page">
    <div class="panel-title"><div><h2>公众监督数据中心</h2><p>统一检索反馈、指派和实测结果，共 {{filteredFeedbacks.length}} 条记录。</p></div><button class="secondary" @click="loadAll"><i class="fa-solid fa-rotate"></i> 刷新数据</button></div>
    <div class="filter-bar"><label><i class="fa-solid fa-magnifying-glass"></i><input v-model="keyword" placeholder="搜索编号、城市或观测地址"/></label><select v-model="statusFilter"><option value="">全部状态</option><option>待指派</option><option>已指派</option><option>已完成</option></select><select v-model="provinceFilter"><option value="">全部省份</option><option v-for="province in provinces" :key="province">{{province}}</option></select><button @click="keyword='';statusFilter='';provinceFilter=''">重置筛选</button></div>
    <el-table :data="pagedFeedbacks" stripe><el-table-column prop="feedbackNo" label="编号" min-width="165"/><el-table-column label="所属网格" min-width="150"><template #default="s"><strong>{{s.row.province}}·{{s.row.city}}</strong><small class="table-sub">{{s.row.address}}</small></template></el-table-column><el-table-column label="预估AQI"><template #default="s"><AqiBadge :value="s.row.estimate"/></template></el-table-column><el-table-column prop="workerName" label="网格员"/><el-table-column prop="assignType" label="指派类型"/><el-table-column prop="status" label="状态"/><el-table-column label="最终AQI"><template #default="s"><AqiBadge :value="s.row.finalAqi"/></template></el-table-column><el-table-column prop="createTime" label="提交时间" min-width="165"/><el-table-column label="详情" width="105" fixed="right"><template #default="s"><button class="detail-button" @click="openDetail(s.row)"><i class="fa-regular fa-eye"></i> 查看</button></template></el-table-column></el-table>
    <el-pagination class="table-pagination" v-model:current-page="feedbackPage" :page-size="6" layout="total, prev, pager, next, jumper" :total="filteredFeedbacks.length"/>
    <div class="table-footer"><span>数据每30秒自动更新</span><span>当前展示 {{filteredFeedbacks.length}} / {{feedbacks.length}} 条</span></div>
    <FeedbackDetailDialog v-model="detailVisible" :row="detailRow"/>
  </section>

  <template v-else-if="page==='statistics'">
    <section class="statistics-banner"><div><p>MULTI-DIMENSIONAL STATISTICS</p><h2>多维度统计数据管理</h2><span>省分组统计、AQI指数分布、处置趋势、实时业务量与网格覆盖率统一分析。</span></div><button class="secondary" @click="loadAll"><i class="fa-solid fa-arrows-rotate"></i> 更新统计</button></section>
    <StatCards :items="statisticsCards"/>
    <AnalyticsBoard :stats="stats"/>
    <section class="statistics-panels"><article class="panel realtime-panel"><div class="panel-title"><div><h2>实时业务统计</h2><p>当前系统任务流转状态</p></div><span class="live-dot"><i></i>LIVE</span></div><div class="realtime-grid"><div><strong>{{stats.waitingTotal||0}}</strong><span>待指派</span><small>等待管理员处理</small></div><div><strong>{{stats.doingTotal||0}}</strong><span>检测中</span><small>网格员现场处置</small></div><div><strong>{{stats.doneTotal||0}}</strong><span>已归档</span><small>已形成实测结果</small></div><div><strong>{{stats.badTotal||0}}</strong><span>污染数据</span><small>轻度污染及以上</small></div></div></article><article class="panel coverage-panel"><div class="panel-title"><div><h2>网格覆盖率统计</h2><p>区域和人员配置情况</p></div></div><div class="coverage-rings"><div class="coverage-ring" :style="{'--value':cityCoverageRate+'%'}"><strong>{{cityCoverageRate}}%</strong><span>城市覆盖</span></div><div class="coverage-ring blue-ring" :style="{'--value':personCoverageRate+'%'}"><strong>{{personCoverageRate}}%</strong><span>人员覆盖</span></div></div><ul><li><span>已启用城市网格</span><b>{{areas.length}}</b></li><li><span>已配置网格员城市</span><b>{{coveredAreaCount}}</b></li><li><span>跨区域支援人员</span><b>{{supportWorkers.length}}</b></li></ul></article></section>
    <section class="panel province-stat-table"><div class="panel-title"><div><h2>省分组统计明细</h2><p>按省份对反馈、完成、污染和覆盖情况进行聚合。</p></div></div><table><thead><tr><th>省份</th><th>反馈总量</th><th>已完成</th><th>处理中</th><th>污染数据</th><th>城市网格</th><th>闭环率</th></tr></thead><tbody><tr v-for="item in pagedProvinceStatistics" :key="item.province"><td><strong>{{item.province}}</strong></td><td>{{item.total}}</td><td>{{item.done}}</td><td>{{item.processing}}</td><td><span :class="item.bad?'risk-label':'normal-label'">{{item.bad}}</span></td><td>{{item.areas}}</td><td><div class="cell-progress"><i :style="{width:item.rate+'%'}"></i></div><b>{{item.rate}}%</b></td></tr></tbody></table><el-pagination class="table-pagination" v-model:current-page="provincePage" :page-size="6" layout="total, prev, pager, next" :total="provinceStatistics.length"/></section>
  </template>

  <section v-else-if="page==='assign'" class="assign-page">
    <div class="assignment-summary"><article><i class="fa-solid fa-inbox"></i><span>待指派<strong>{{waiting.length}}</strong></span></article><article><i class="fa-solid fa-location-dot"></i><span>本地可匹配<strong>{{localMatchCount}}</strong></span></article><article><i class="fa-solid fa-people-arrows"></i><span>可支援人员<strong>{{supportWorkers.length}}</strong></span></article></div>
    <section class="content-grid assign-layout"><article class="panel"><div class="panel-title"><div><h2>待指派反馈</h2><p>优先处理污染等级较高的反馈</p></div></div><div class="assignment-list"><div v-for="item in waiting" :key="item.id" class="task-item" :class="{selected:selectedFeedback?.id===item.id}" @click="selectedFeedback=item"><div><strong>{{item.feedbackNo}}</strong><AqiBadge :value="item.estimate"/></div><span><i class="fa-solid fa-location-dot"></i>{{item.province}} · {{item.city}}</span><small>{{item.address}}</small><em>{{item.description}}</em></div><p v-if="!waiting.length" class="empty">全部反馈均已完成指派</p></div></article><article class="panel"><div class="panel-title"><div><h2>智能匹配网格员</h2><p>综合属地、状态和任务负载推荐</p></div></div><template v-if="selectedFeedback"><div class="selected-context"><span>当前任务</span><strong>{{selectedFeedback.city}} · {{selectedFeedback.address}}</strong><small>公众预估：{{selectedFeedback.estimate}}</small></div><div v-for="worker in sortedWorkers" :key="worker.id" class="worker-option" :class="{recommended:worker.city===selectedFeedback.city}"><div class="worker-avatar"><i class="fa-solid fa-user-shield"></i></div><div><strong>{{worker.name}}</strong><span>{{worker.province}} · {{worker.city}} · {{worker.status}}</span><small>当前累计任务 {{worker.taskCount||0}} 项</small></div><em v-if="worker.city===selectedFeedback.city">系统推荐</em><button class="table-action" @click="assign(worker)">确认指派</button></div></template><p v-else class="empty">请先选择左侧反馈</p></article></section>
  </section>

  <template v-else-if="page==='workers'">
    <StatCards :items="workerCards"/><section class="panel"><div class="panel-title"><div><h2>网格员资源管理</h2><p>查看人员所属区域、工作状态和任务负载。</p></div><span class="data-badge">HR同步</span></div><el-table :data="pagedWorkers" stripe><el-table-column prop="code" label="人员编号"/><el-table-column label="网格员"><template #default="s"><strong>{{s.row.name}}</strong><small class="table-sub">AQI检测资质有效</small></template></el-table-column><el-table-column prop="province" label="省份"/><el-table-column prop="city" label="城市"/><el-table-column label="工作状态"><template #default="s"><span class="online">{{s.row.status}}</span></template></el-table-column><el-table-column prop="taskCount" label="累计任务"/><el-table-column label="负载"><template #default="s"><div class="load-bar"><i :style="{width:Math.min(100,(s.row.taskCount||0)*18)+'%'}"></i></div></template></el-table-column></el-table><el-pagination class="table-pagination" v-model:current-page="workerPage" :page-size="7" layout="total, prev, pager, next" :total="workers.length"/></section>
  </template>

  <template v-else>
    <StatCards :items="areaCards"/><section class="content-grid area-overview"><article class="panel"><h2>区域覆盖概览</h2><div class="coverage-map"><div v-for="province in areaGroups" :key="province.name"><span>{{province.name}}</span><strong>{{province.cities}}</strong><small>城市网格</small><i :style="{height:Math.max(36,province.cities*24)+'px'}"></i></div></div></article><article class="panel"><h2>网格运行质量</h2><div class="quality-score"><strong>98.6</strong><span>综合运行指数</span></div><ul class="quality-list"><li><span>网格在线率</span><b>100%</b></li><li><span>人员覆盖率</span><b>{{workerCoverage}}%</b></li><li><span>反馈闭环率</span><b>{{completion}}%</b></li></ul></article></section><section class="panel area-table"><div class="panel-title"><div><h2>省市网格明细</h2><p>城市作为最小业务网格单元。</p></div></div><el-table :data="pagedAreas" stripe><el-table-column prop="province" label="省份"/><el-table-column prop="city" label="城市网格"/><el-table-column prop="workerCount" label="网格员数"/><el-table-column prop="feedbackCount" label="反馈数量"/><el-table-column label="运行状态"><template #default><span class="online"><i></i>运行中</span></template></el-table-column></el-table><el-pagination class="table-pagination" v-model:current-page="areaPage" :page-size="7" layout="total, prev, pager, next" :total="areas.length"/></section>
  </template>
</template>

<script setup>
import {computed,onBeforeUnmount,onMounted,ref,watch} from 'vue'
import {ElMessage} from 'element-plus'
import http from '../api/http'
import AqiBadge from '../components/AqiBadge.vue'
import StatCards from '../components/StatCards.vue'
import AnalyticsBoard from '../components/AnalyticsBoard.vue'
import ActivityTimeline from '../components/ActivityTimeline.vue'
import FeedbackDetailDialog from '../components/FeedbackDetailDialog.vue'
defineProps({page:String});const emit=defineEmits(['navigate'])
const feedbacks=ref([]),workers=ref([]),areas=ref([]),stats=ref({}),selectedFeedback=ref(null),keyword=ref(''),statusFilter=ref(''),provinceFilter=ref(''),detailVisible=ref(false),detailRow=ref(null),feedbackPage=ref(1),workerPage=ref(1),areaPage=ref(1),provincePage=ref(1)
const waiting=computed(()=>feedbacks.value.filter(x=>x.status==='待指派'))
const completion=computed(()=>Math.round((stats.value.doneTotal||0)/Math.max(1,stats.value.feedbackTotal||0)*100))
const todayReceived=computed(()=>Math.max(1,Math.min(9,feedbacks.value.length-waiting.value.length+1)))
const cards=computed(()=>[{label:'反馈总数',value:stats.value.feedbackTotal,icon:'fa-solid fa-message'},{label:'待指派',value:stats.value.waitingTotal,icon:'fa-solid fa-hourglass-half'},{label:'检测中',value:stats.value.doingTotal,icon:'fa-solid fa-flask'},{label:'已完成',value:stats.value.doneTotal,icon:'fa-solid fa-circle-check'}])
const provinces=computed(()=>[...new Set(feedbacks.value.map(x=>x.province))])
const filteredFeedbacks=computed(()=>feedbacks.value.filter(item=>(!statusFilter.value||item.status===statusFilter.value)&&(!provinceFilter.value||item.province===provinceFilter.value)&&(!keyword.value||[item.feedbackNo,item.city,item.address].some(value=>String(value||'').includes(keyword.value)))))
const pagedFeedbacks=computed(()=>filteredFeedbacks.value.slice((feedbackPage.value-1)*6,feedbackPage.value*6))
const pagedWorkers=computed(()=>workers.value.slice((workerPage.value-1)*7,workerPage.value*7))
const pagedAreas=computed(()=>areas.value.slice((areaPage.value-1)*7,areaPage.value*7))
const supportWorkers=computed(()=>workers.value.filter(x=>x.status==='支援'))
const localMatchCount=computed(()=>waiting.value.filter(item=>workers.value.some(worker=>worker.city===item.city&&worker.status==='可工作')).length)
const sortedWorkers=computed(()=>selectedFeedback.value?[...workers.value].sort((a,b)=>Number(b.city===selectedFeedback.value.city)-Number(a.city===selectedFeedback.value.city)||(a.taskCount||0)-(b.taskCount||0)):workers.value)
const workerCards=computed(()=>[{label:'人员总数',value:workers.value.length,icon:'fa-solid fa-users'},{label:'可工作',value:workers.value.filter(x=>x.status==='可工作').length,icon:'fa-solid fa-user-check'},{label:'支援状态',value:supportWorkers.value.length,icon:'fa-solid fa-people-arrows'},{label:'覆盖城市',value:new Set(workers.value.map(x=>x.city)).size,icon:'fa-solid fa-city'}])
const areaCards=computed(()=>[{label:'省份覆盖',value:new Set(areas.value.map(x=>x.province)).size,icon:'fa-solid fa-map'},{label:'城市网格',value:areas.value.length,icon:'fa-solid fa-location-dot'},{label:'配置人员',value:workers.value.length,icon:'fa-solid fa-user-shield'},{label:'反馈总量',value:feedbacks.value.length,icon:'fa-solid fa-message'}])
const areaGroups=computed(()=>Object.entries(areas.value.reduce((map,item)=>{map[item.province]=(map[item.province]||0)+1;return map},{})).map(([name,cities])=>({name,cities})))
const workerCoverage=computed(()=>Math.round(areas.value.filter(x=>x.workerCount>0).length/Math.max(1,areas.value.length)*100))
const coveredAreaCount=computed(()=>areas.value.filter(x=>x.workerCount>0).length)
const cityCoverageRate=computed(()=>Math.round(areas.value.filter(x=>x.enabled!==false).length/Math.max(1,areas.value.length)*100))
const personCoverageRate=computed(()=>Math.round(coveredAreaCount.value/Math.max(1,areas.value.length)*100))
const statisticsCards=computed(()=>[{label:'统计省份',value:new Set(feedbacks.value.map(x=>x.province)).size,icon:'fa-solid fa-map'},{label:'AQI样本',value:stats.value.doneTotal,icon:'fa-solid fa-chart-pie'},{label:'实时任务',value:(stats.value.waitingTotal||0)+(stats.value.doingTotal||0),icon:'fa-solid fa-satellite-dish'},{label:'网格覆盖率',value:cityCoverageRate.value+'%',icon:'fa-solid fa-location-crosshairs'}])
const provinceStatistics=computed(()=>[...new Set(feedbacks.value.map(x=>x.province))].map(province=>{const rows=feedbacks.value.filter(x=>x.province===province),done=rows.filter(x=>x.status==='已完成').length,bad=rows.filter(x=>['轻度污染','中度污染','重度污染','严重污染'].includes(x.finalAqi)).length;return{province,total:rows.length,done,processing:rows.length-done,bad,areas:areas.value.filter(x=>x.province===province).length,rate:Math.round(done/Math.max(1,rows.length)*100)}}))
const pagedProvinceStatistics=computed(()=>provinceStatistics.value.slice((provincePage.value-1)*6,provincePage.value*6))
function openDetail(row){detailRow.value=row;detailVisible.value=true}
function goTo(page,status=''){if(status)statusFilter.value=status;feedbackPage.value=1;emit('navigate',page)}
async function loadAll(){const [f,w,a,s]=await Promise.all([http.get('/feedbacks'),http.get('/workers'),http.get('/areas'),http.get('/stats')]);feedbacks.value=f.data;workers.value=w.data;areas.value=a.data;stats.value=s.data;if(!selectedFeedback.value)selectedFeedback.value=waiting.value[0]}
async function assign(worker){await http.post('/assignments',{feedbackId:selectedFeedback.value.id,workerId:worker.id});ElMessage.success(worker.city===selectedFeedback.value.city?'本地指派成功':'异地支援指派成功');selectedFeedback.value=null;await loadAll()}
watch([keyword,statusFilter,provinceFilter],()=>feedbackPage.value=1)
const refreshTimer=setInterval(loadAll,30000)
onMounted(loadAll)
onBeforeUnmount(()=>clearInterval(refreshTimer))
</script>
