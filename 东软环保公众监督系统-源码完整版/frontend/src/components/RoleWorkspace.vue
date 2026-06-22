<template>
  <main class="workspace">
    <aside class="sidebar">
      <div class="side-brand"><i class="fa-solid fa-leaf"></i><span>东软环保<br/>公众监督系统</span></div>
      <div class="userbox"><strong>{{ roleName }}</strong><span>{{ session.name }} / {{ session.account }}</span></div>
      <button v-for="item in menus" :key="item.key" :class="{active: page === item.key}" @click="page = item.key"><i :class="item.icon"></i>{{ item.label }}</button>
      <button class="logout" @click="$emit('logout')"><i class="fa-solid fa-right-from-bracket"></i>退出登录</button>
    </aside>
    <section class="main-area">
      <header class="page-head">
        <div><p>NEP AQI PLATFORM / {{ roleCode }}</p><h1>{{ currentMenu.label }}</h1><span>{{ currentMenu.desc }}</span></div>
        <div class="head-tools"><span class="system-online"><i></i>系统运行正常</span><time>{{ now }}</time></div>
      </header>
      <PublicView v-if="session.role === 'neps'" :page="page" :session="session" @navigate="page=$event" />
      <WorkerView v-else-if="session.role === 'nepg'" :page="page" :session="session" @navigate="page=$event" />
      <AdminView v-else-if="session.role === 'nepm'" :page="page" @navigate="page=$event" />
      <DecisionView v-else :page="page" />
    </section>
  </main>
</template>

<script setup>
import { computed, onBeforeUnmount, ref } from 'vue'
import PublicView from '../views/PublicView.vue'
import WorkerView from '../views/WorkerView.vue'
import AdminView from '../views/AdminView.vue'
import DecisionView from '../views/DecisionView.vue'
const props = defineProps({ session:Object })
defineEmits(['logout'])
const menuMap = {
  neps:[['home','公众首页','fa-solid fa-house','查看反馈概况和最新处理动态'],['submit','提交反馈','fa-solid fa-pen-to-square','上报空气质量异常并选择所属网格'],['history','历史记录','fa-solid fa-clock-rotate-left','追踪全部反馈的指派与检测进度'],['profile','个人中心','fa-solid fa-id-card','管理公众监督员注册资料']],
  nepg:[['tasks','任务工作台','fa-solid fa-list-check','查看指派任务和今日工作安排'],['measure','实测录入','fa-solid fa-flask','录入三项污染物等级并计算最终AQI'],['work','工作状态','fa-solid fa-gauge-high','查看任务负载、完成记录和服务质量']],
  nepm:[['dashboard','管理看板','fa-solid fa-chart-pie','全局掌握监督业务运行与处置效率'],['feedbacks','监督数据','fa-solid fa-table-list','检索公众反馈和实测结果'],['assign','任务指派','fa-solid fa-route','按属地与负载智能匹配网格员'],['statistics','多维统计','fa-solid fa-chart-column','省分组、指数分布、趋势、实时与覆盖率统计'],['workers','网格员管理','fa-solid fa-people-group','查看人员区域和任务负载'],['areas','网格区域','fa-solid fa-map-location-dot','管理省市网格覆盖范围']],
  nepv:[['screen','可视化大屏','fa-solid fa-display','汇总AQI分布、区域热度和闭环效率'],['statistics','统计数据','fa-solid fa-chart-column','多维分析公众监督业务数据'],['report','决策报告','fa-solid fa-file-lines','生成治理研判与决策建议']]
}
const menus = computed(() => menuMap[props.session.role].map(([key,label,icon,desc]) => ({key,label,icon,desc})))
const page = ref(menus.value[0].key)
const currentMenu = computed(() => menus.value.find(item => item.key === page.value))
const roleName = computed(() => ({neps:'NEPS 公众监督员端',nepg:'NEPG AQI检测网格员端',nepm:'NEPM 系统管理端',nepv:'NEPV 决策者端'})[props.session.role])
const roleCode = computed(() => props.session.role.toUpperCase())
const format = () => new Date().toLocaleString('zh-CN',{hour12:false})
const now = ref(format())
const timer = setInterval(() => now.value = format(), 1000)
onBeforeUnmount(() => clearInterval(timer))
</script>
