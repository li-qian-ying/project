<template>
  <main class="workspace">
    <aside class="sidebar">
      <div class="side-brand"><i class="fa-solid fa-leaf"></i><span>东软环保<br/>公众监督系统</span></div>
      <div class="userbox"><strong>{{ roleName }}</strong><span>{{ session.name }} / {{ session.account }}</span></div>
      <button v-for="item in menus" :key="item.key" :class="{active: page === item.key}" @click="page = item.key"><i :class="item.icon"></i>{{ item.label }}</button>
      <button class="logout" @click="$emit('logout')"><i class="fa-solid fa-right-from-bracket"></i>退出登录</button>
    </aside>
    <section class="main-area">
      <header class="page-head"><div><p>NEP AQI PLATFORM</p><h1>{{ currentMenu.label }}</h1></div><time>{{ now }}</time></header>
      <PublicView v-if="session.role === 'neps'" :page="page" :session="session" />
      <WorkerView v-else-if="session.role === 'nepg'" :page="page" :session="session" />
      <AdminView v-else-if="session.role === 'nepm'" :page="page" />
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
  neps:[['submit','提交反馈','fa-solid fa-pen-to-square'],['history','历史记录','fa-solid fa-clock-rotate-left'],['profile','个人中心','fa-solid fa-id-card']],
  nepg:[['tasks','任务工作台','fa-solid fa-list-check'],['measure','实测录入','fa-solid fa-flask'],['work','工作状态','fa-solid fa-gauge-high']],
  nepm:[['dashboard','管理看板','fa-solid fa-chart-pie'],['feedbacks','监督数据','fa-solid fa-table-list'],['assign','任务指派','fa-solid fa-route'],['workers','网格员管理','fa-solid fa-people-group'],['areas','网格区域','fa-solid fa-map-location-dot']],
  nepv:[['screen','可视化大屏','fa-solid fa-display'],['statistics','统计数据','fa-solid fa-chart-column'],['report','决策报告','fa-solid fa-file-lines']]
}
const menus = computed(() => menuMap[props.session.role].map(([key,label,icon]) => ({key,label,icon})))
const page = ref(menus.value[0].key)
const currentMenu = computed(() => menus.value.find(item => item.key === page.value))
const roleName = computed(() => ({neps:'NEPS 公众监督员端',nepg:'NEPG AQI检测网格员端',nepm:'NEPM 系统管理端',nepv:'NEPV 决策者端'})[props.session.role])
const format = () => new Date().toLocaleString('zh-CN',{hour12:false})
const now = ref(format())
const timer = setInterval(() => now.value = format(), 1000)
onBeforeUnmount(() => clearInterval(timer))
</script>
