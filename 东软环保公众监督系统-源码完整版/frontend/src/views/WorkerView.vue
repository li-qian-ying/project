<template>
  <template v-if="page==='tasks'">
    <StatCards :items="stats"/>
    <section class="worker-summary-grid">
      <article class="panel shift-card"><div><p>TODAY SHIFT</p><h2>{{session.city}}网格巡检</h2><span>08:30 - 17:30 · 当前状态：{{session.workStatus}}</span></div><div class="shift-score"><strong>{{completion}}%</strong><span>任务完成率</span></div></article>
      <article class="panel sla-card"><div><span>平均响应</span><strong>18<small>分钟</small></strong></div><div><span>按时完成</span><strong>96<small>%</small></strong></div><div><span>数据合格</span><strong>100<small>%</small></strong></div></article>
    </section>
    <section class="panel">
      <div class="panel-title"><div><h2>检测任务队列</h2><p>任务按污染等级和指派时间自动排序。</p></div><div class="table-tools"><label class="task-search"><i class="fa-solid fa-magnifying-glass"></i><input v-model="taskKeyword" placeholder="搜索编号或地址"/></label><button class="filter-chip" :class="{active:taskFilter==='all'}" @click="setTaskFilter('all')">全部 {{tasks.length}}</button><button class="filter-chip" :class="{active:taskFilter==='pending'}" @click="setTaskFilter('pending')">待检测 {{pending.length}}</button><button class="filter-chip" :class="{active:taskFilter==='done'}" @click="setTaskFilter('done')">已完成 {{done.length}}</button><button class="secondary" @click="load"><i class="fa-solid fa-rotate"></i> 刷新</button></div></div>
      <el-table :data="pagedTasks" stripe>
        <el-table-column prop="feedbackNo" label="任务编号" min-width="170"/>
        <el-table-column prop="province" label="省份"/><el-table-column prop="city" label="城市"/>
        <el-table-column prop="address" label="观测地址" min-width="210"/>
        <el-table-column label="预估AQI"><template #default="s"><AqiBadge :value="s.row.estimate"/></template></el-table-column>
        <el-table-column prop="assignType" label="指派类型"/><el-table-column prop="status" label="状态"/>
        <el-table-column label="操作" width="200"><template #default="s"><div class="row-actions"><button class="detail-button" @click="openDetail(s.row)"><i class="fa-regular fa-eye"></i> 查看</button><button v-if="s.row.status==='已指派'" class="table-action" @click="goMeasure(s.row)"><i class="fa-solid fa-flask"></i> 检测</button></div></template></el-table-column>
      </el-table>
      <el-pagination class="table-pagination" v-model:current-page="taskPage" :page-size="6" layout="total, prev, pager, next" :total="filteredTasks.length"/>
      <FeedbackDetailDialog v-model="detailVisible" :row="detailRow"/>
    </section>
    <section class="content-grid worker-bottom-grid"><article class="panel"><h2>今日巡检路线</h2><div class="route-list"><button v-for="(item,index) in pending.slice(0,4)" :key="item.id" @click="goMeasure(item)"><b>{{String(index+1).padStart(2,'0')}}</b><span><strong>{{item.address}}</strong><small>{{item.city}} · {{item.estimate}} · {{item.assignType}}</small></span><i class="fa-solid fa-chevron-right"></i></button><p v-if="!pending.length" class="empty">今日指派任务已全部完成</p></div></article><article class="panel"><h2>现场检测规范</h2><ul class="check-list"><li><i class="fa-solid fa-check"></i>确认设备校准与定位状态</li><li><i class="fa-solid fa-check"></i>现场停留不少于规定采样时间</li><li><i class="fa-solid fa-check"></i>分别核验SO2、CO、PM2.5等级</li><li><i class="fa-solid fa-check"></i>提交前复核地址和任务编号</li></ul></article></section>
  </template>

  <section v-else-if="page==='measure'" class="measure-layout">
    <article class="panel form-panel"><div class="panel-title"><div><h2>实测数据录入</h2><p>选择已指派任务，依次录入三项污染物等级。</p></div><span class="required-tip">三项必填</span></div><label>检测任务<select v-model="selectedId"><option v-for="item in pending" :key="item.id" :value="item.id">{{item.feedbackNo}} · {{item.city}} · {{item.address}}</option></select></label><div v-if="selected" class="task-context"><i class="fa-solid fa-location-dot"></i><div><strong>{{selected.address}}</strong><span>{{selected.province}} {{selected.city}} · 公众预估 {{selected.estimate}}</span></div></div><div class="pollutant-grid"><label v-for="field in pollutantFields" :key="field.key"><span><i :class="field.icon"></i>{{field.label}}</span><select v-model="form[field.key]"><option v-for="item in levels" :key="item">{{item}}</option></select><small>{{field.desc}}</small></label></div><button class="primary compact" @click="submit"><i class="fa-solid fa-cloud-arrow-up"></i> 提交实测结果</button></article>
    <article class="panel result-panel"><div class="aqi-ring" :class="`ring-${levelIndex}`"><div><span>最终AQI</span><strong>{{finalLevel}}</strong></div></div><p>AQI = MAX（SO2，CO，PM2.5）</p><small>系统自动取三项污染物中等级最高的一项</small><div class="result-breakdown"><span v-for="field in pollutantFields" :key="field.key"><b>{{field.short}}</b><AqiBadge :value="form[field.key]"/></span></div></article>
  </section>

  <template v-else>
    <StatCards :items="stats"/>
    <section class="content-grid"><article class="panel"><h2>当前工作状态</h2><div class="status-line"><i class="fa-solid fa-circle-check"></i><div><strong>{{session.workStatus}}</strong><span>{{session.province}} · {{session.city}}</span></div></div><div class="workload-meter"><span>当前任务负载</span><div><i :style="{width:Math.min(100,pending.length*22)+'%'}"></i></div><b>{{pending.length}} 项</b></div><p>系统优先分配同城市任务；任务积压时由管理员启动异地支援。</p></article><article class="panel"><h2>最近完成记录</h2><ul class="simple-list"><li v-for="item in done.slice(0,6)" :key="item.id"><span><strong>{{item.feedbackNo}}</strong><small>{{item.city}} · {{formatTime(item.measureTime)}}</small></span><AqiBadge :value="item.finalAqi"/></li></ul></article></section>
  </template>
</template>

<script setup>
import {computed,onMounted,reactive,ref,watch} from 'vue'
import {ElMessage} from 'element-plus'
import http from '../api/http'
import AqiBadge from '../components/AqiBadge.vue'
import StatCards from '../components/StatCards.vue'
import FeedbackDetailDialog from '../components/FeedbackDetailDialog.vue'
const props=defineProps({page:String,session:Object});const emit=defineEmits(['navigate'])
const tasks=ref([]),selectedId=ref(null),detailVisible=ref(false),detailRow=ref(null),taskFilter=ref('all'),taskPage=ref(1),taskKeyword=ref('')
const levels=['优','良','轻度污染','中度污染','重度污染','严重污染']
const form=reactive({so2:'优',co:'优',pm25:'优'})
const pollutantFields=[{key:'so2',short:'SO2',label:'SO2 二氧化硫',icon:'fa-solid fa-industry',desc:'燃煤及工业排放特征污染物'},{key:'co',short:'CO',label:'CO 一氧化碳',icon:'fa-solid fa-car-side',desc:'交通及不完全燃烧特征污染物'},{key:'pm25',short:'PM2.5',label:'PM2.5 细颗粒物',icon:'fa-solid fa-smog',desc:'影响能见度和健康的细颗粒物'}]
const pending=computed(()=>tasks.value.filter(x=>x.status==='已指派')),done=computed(()=>tasks.value.filter(x=>x.status==='已完成'))
const filteredTasks=computed(()=>{const base=taskFilter.value==='pending'?pending.value:taskFilter.value==='done'?done.value:tasks.value;return base.filter(item=>!taskKeyword.value||[item.feedbackNo,item.province,item.city,item.address].some(value=>String(value||'').includes(taskKeyword.value)))}),pagedTasks=computed(()=>filteredTasks.value.slice((taskPage.value-1)*6,taskPage.value*6))
const selected=computed(()=>tasks.value.find(x=>x.id===selectedId.value))
const levelIndex=computed(()=>Math.max(levels.indexOf(form.so2),levels.indexOf(form.co),levels.indexOf(form.pm25)))
const finalLevel=computed(()=>levels[levelIndex.value])
const completion=computed(()=>Math.round(done.value.length/Math.max(1,tasks.value.length)*100))
const stats=computed(()=>[{label:'任务总数',value:tasks.value.length,icon:'fa-solid fa-list-check'},{label:'待检测',value:pending.value.length,icon:'fa-solid fa-hourglass-half'},{label:'已完成',value:done.value.length,icon:'fa-solid fa-circle-check'},{label:'工作状态',value:props.session.workStatus,icon:'fa-solid fa-user-clock'}])
function openDetail(row){detailRow.value=row;detailVisible.value=true}
function setTaskFilter(value){taskFilter.value=value;taskPage.value=1}
function goMeasure(item){selectedId.value=item.id;emit('navigate','measure')}
function formatTime(value){return value?String(value).replace('T',' ').slice(0,16):'-'}
async function load(){tasks.value=(await http.get('/feedbacks',{params:{workerId:props.session.id}})).data;if(!selectedId.value&&pending.value[0])selectedId.value=pending.value[0].id}
async function submit(){if(!selectedId.value)return ElMessage.warning('当前没有可录入任务');await http.post('/measurements',{feedbackId:selectedId.value,workerId:props.session.id,...form});ElMessage.success('实测数据已入库并完成AQI计算');selectedId.value=null;await load()}
watch(taskKeyword,()=>taskPage.value=1)
onMounted(load)
</script>
