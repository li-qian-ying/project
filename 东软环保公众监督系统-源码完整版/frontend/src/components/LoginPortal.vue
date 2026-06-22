<template>
  <main class="portal">
    <header class="brandbar">
      <img src="/assets/neuedu.png" alt="Neuedu" />
      <strong>东软环保公众监督系统</strong>
      <span>空气质量公众监督与决策支持平台</span>
    </header>
    <section class="hero">
      <div class="hero-copy">
        <p class="eyebrow">AQI PUBLIC SUPERVISION</p>
        <h1>空气质量公众监督与决策支持平台</h1>
        <p>连接公众反馈、网格检测、系统管理与决策分析，形成可追踪的环保业务闭环。</p>
        <div class="flow-tags"><span>公众上报</span><span>智能指派</span><span>实测核验</span><span>AQI计算</span><span>决策分析</span></div>
        <section class="portal-preview">
          <header><div><span>业务闭环实时状态</span><strong>四端协同运行中</strong></div><em><i></i>服务正常</em></header>
          <div class="preview-flow"><article><i class="fa-solid fa-users"></i><b>NEPS</b><span>公众上报</span></article><i class="fa-solid fa-arrow-right"></i><article><i class="fa-solid fa-layer-group"></i><b>NEPM</b><span>审核指派</span></article><i class="fa-solid fa-arrow-right"></i><article><i class="fa-solid fa-flask-vial"></i><b>NEPG</b><span>现场实测</span></article><i class="fa-solid fa-arrow-right"></i><article><i class="fa-solid fa-chart-line"></i><b>NEPV</b><span>辅助决策</span></article></div>
          <footer><span><b>24</b>城市网格</span><span><b>96%</b>按时处置</span><span><b>100%</b>数据可追溯</span></footer>
        </section>
      </div>
      <section class="login-panel">
        <div class="role-tabs">
          <button v-for="item in roles" :key="item.key" :class="{active: form.role === item.key}" @click="selectRole(item)">
            <i :class="item.icon"></i><span>{{ item.short }}</span>
          </button>
        </div>
        <h2>{{ registerMode ? '公众监督员注册' : current.title }}</h2>
        <p>{{ registerMode ? '只有公众监督员可自主注册，其他角色账号由系统管理员统一创建。' : current.desc }}</p>
        <template v-if="registerMode">
          <label>真实姓名<input v-model="register.realName" placeholder="请输入真实姓名" /></label>
          <label>手机号<input v-model="register.phone" maxlength="11" placeholder="用于登录的11位手机号" /></label>
          <div class="register-row"><label>年龄<input v-model.number="register.age" type="number" min="1" max="120" /></label><label>性别<select v-model="register.gender"><option>女</option><option>男</option></select></label></div>
          <label>登录密码<input v-model="register.password" type="password" placeholder="至少6位" /></label>
          <button class="primary" :disabled="loading" @click="submitRegister">{{ loading ? '正在注册...' : '注册并进入系统' }}</button>
          <button class="text-button" @click="registerMode=false">已有账号，返回登录</button>
        </template>
        <template v-else>
          <label>登录账号<input v-model="form.account" /></label>
          <label>登录密码<input v-model="form.password" type="password" /></label>
          <button class="primary" :disabled="loading" @click="submit">{{ loading ? '正在登录...' : '进入系统' }}</button>
          <button v-if="form.role==='neps'" class="text-button" @click="registerMode=true">没有账号？注册公众监督员</button>
          <small>当前演示账号：{{ form.account }} / 123456</small>
        </template>
      </section>
    </section>
  </main>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const emit = defineEmits(['login'])
const roles = [
  { key:'neps', short:'NEPS', title:'公众监督员端', account:'13924689016', icon:'fa-solid fa-users', desc:'提交空气质量反馈并追踪处理进度' },
  { key:'nepg', short:'NEPG', title:'AQI检测网格员端', account:'grid001', icon:'fa-solid fa-flask-vial', desc:'接收任务并录入三项污染物实测等级' },
  { key:'nepm', short:'NEPM', title:'系统管理端', account:'admin001', icon:'fa-solid fa-layer-group', desc:'管理反馈、指派网格员并统计分析' },
  { key:'nepv', short:'NEPV', title:'决策者端', account:'decision001', icon:'fa-solid fa-chart-line', desc:'查看可视化大屏和决策分析报告' }
]
const form = reactive({ role:'neps', account:roles[0].account, password:'123456' })
const register = reactive({ realName:'', phone:'', age:22, gender:'女', password:'123456' })
const registerMode = ref(false)
const loading = ref(false)
const current = computed(() => roles.find(item => item.key === form.role))
function selectRole(item) { form.role = item.key; form.account = item.account; registerMode.value = false }
async function submit() {
  loading.value = true
  try { emit('login', (await http.post('/auth/login', form)).data) }
  catch (error) { ElMessage.error(error.response?.data?.message || '登录失败，请先启动后端和数据库') }
  finally { loading.value = false }
}
async function submitRegister() {
  loading.value = true
  try { emit('login', (await http.post('/auth/register', register)).data); ElMessage.success('注册成功') }
  catch (error) { ElMessage.error(error.response?.data?.message || '注册失败，请检查后端和数据库') }
  finally { loading.value = false }
}
</script>
