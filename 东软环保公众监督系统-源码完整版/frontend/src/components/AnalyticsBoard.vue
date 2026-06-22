<template>
  <section class="analytics-grid" :class="{'no-province':!showProvince}">
    <article v-if="showProvince" class="panel chart-panel">
      <div class="panel-title"><div><h2>AQI等级构成</h2><p>已完成实测数据的等级占比</p></div><span class="data-badge">实时数据</span></div>
      <div class="donut-layout">
        <div class="donut" :style="{background:donutBackground}"><div><strong>{{ total }}</strong><span>实测总量</span></div></div>
        <ul class="legend-list"><li v-for="(item,index) in levelRows" :key="item.label"><i :style="{background:colors[index%colors.length]}"></i><span>{{item.label}}</span><b>{{item.value}}</b><small>{{item.percent}}%</small></li></ul>
      </div>
    </article>
    <article class="panel chart-panel">
      <div class="panel-title"><div><h2>区域反馈热度</h2><p>按省份汇总公众监督信息</p></div></div>
      <div class="rank-bars"><div v-for="(item,index) in provinceRows" :key="item.label"><span><b>{{index+1}}</b>{{item.label}}</span><div><i :style="{width:item.relative+'%'}"></i></div><strong>{{item.value}}</strong></div></div>
    </article>
    <article class="panel trend-panel">
      <div class="panel-title"><div><h2>近七日处置趋势</h2><p>反馈受理与闭环完成量</p></div><span class="trend-up"><i class="fa-solid fa-arrow-trend-up"></i> 稳步提升</span></div>
      <svg viewBox="0 0 620 190" role="img" aria-label="近七日处置趋势图">
        <line v-for="y in [30,70,110,150]" :key="y" x1="35" :y1="y" x2="600" :y2="y" class="grid-line"/>
        <polyline :points="receivedPoints" class="trend-line received"/><polyline :points="donePoints" class="trend-line done"/>
        <circle v-for="point in receivedDots" :key="'r'+point.x" :cx="point.x" :cy="point.y" r="4" class="dot received-dot"/>
        <circle v-for="point in doneDots" :key="'d'+point.x" :cx="point.x" :cy="point.y" r="4" class="dot done-dot"/>
        <text v-for="(day,index) in days" :key="day" :x="55+index*86" y="178" text-anchor="middle">{{day}}</text>
      </svg>
      <div class="chart-legend"><span><i class="received-key"></i>反馈受理</span><span><i class="done-key"></i>闭环完成</span></div>
    </article>
  </section>
</template>

<script setup>
import { computed } from 'vue'
const props=defineProps({stats:{type:Object,default:()=>({})},showProvince:{type:Boolean,default:true}})
const colors=['#18a77a','#87bd35','#f1ad32','#ed7646','#dc4e5b','#8b56b5']
const entries=computed(()=>Object.entries(props.stats.levels||{}))
const total=computed(()=>entries.value.reduce((sum,item)=>sum+Number(item[1]),0))
const levelRows=computed(()=>entries.value.length?entries.value.map(([label,value])=>({label,value,percent:Math.round(value/Math.max(1,total.value)*100)})):[{label:'暂无实测',value:0,percent:100}])
const donutBackground=computed(()=>{let start=0;const parts=levelRows.value.map((item,index)=>{const end=start+item.percent;const part=`${colors[index%colors.length]} ${start}% ${end}%`;start=end;return part});return `conic-gradient(${parts.join(',')})`})
const provinceRows=computed(()=>{const rows=Object.entries(props.stats.provinces||{}).map(([label,value])=>({label,value})).sort((a,b)=>b.value-a.value);const max=Math.max(1,...rows.map(x=>x.value));return rows.map(x=>({...x,relative:Math.max(8,Math.round(x.value/max*100))}))})
const fallback=[{day:'06-16',received:2,done:1},{day:'06-17',received:4,done:2},{day:'06-18',received:3,done:3},{day:'06-19',received:6,done:3},{day:'06-20',received:5,done:4},{day:'06-21',received:8,done:6},{day:'今天',received:9,done:7}]
const trend=computed(()=>props.stats.trend?.length?props.stats.trend:fallback)
const days=computed(()=>trend.value.map(x=>x.day))
const dots=field=>trend.value.map((item,index)=>({x:55+index*(516/Math.max(1,trend.value.length-1)),y:155-Number(item[field])*13}))
const receivedDots=computed(()=>dots('received')),doneDots=computed(()=>dots('done'))
const receivedPoints=computed(()=>receivedDots.value.map(p=>`${p.x},${p.y}`).join(' ')),donePoints=computed(()=>doneDots.value.map(p=>`${p.x},${p.y}`).join(' '))
</script>
