<template>
  <template v-if="page==='home'">
    <section class="welcome-banner"><div><p>WELCOME BACK</p><h2>{{session.name}}，欢迎参与空气质量公众监督</h2><span>你的每一次反馈，都会进入网格核验与决策分析闭环。</span></div><button class="banner-action" @click="$emit('navigate','submit')"><i class="fa-solid fa-plus"></i> 发起新反馈</button></section>
    <StatCards :items="profileStats"/>
    <section class="content-grid public-home-grid">
      <ActivityTimeline :items="rows"/>
      <article class="panel aqi-guide"><div class="panel-title"><div><h2>AQI快速判断</h2><p>现场预估等级参考</p></div></div><div v-for="(item,index) in levels" :key="item" class="guide-row"><AqiBadge :value="item"/><div><strong>{{guideTitle[index]}}</strong><span>{{guideText[index]}}</span></div></div></article>
    </section>
    <section class="panel service-path"><div class="panel-title"><div><h2>我的监督服务路径</h2><p>每条反馈均可追踪、可核验、可统计</p></div></div><div class="path-grid"><article v-for="(item,index) in flow" :key="item"><b>{{index+1}}</b><i :class="flowIcons[index]"></i><strong>{{item}}</strong><span>{{flowDesc[index]}}</span></article></div></section>
  </template>
  <section v-else-if="page==='submit'" class="content-grid">
    <article class="panel form-panel"><h2>空气质量监督反馈</h2><p>请填写真实观测地点和现场情况，系统将自动进入指派流程。</p>
      <div class="form-grid">
        <label>省份<select v-model="form.province" @change="form.city=cities[form.province][0]"><option v-for="(_,p) in cities" :key="p">{{p}}</option></select></label>
        <label>城市<select v-model="form.city"><option v-for="city in cities[form.province]" :key="city">{{city}}</option></select></label>
        <label class="wide">具体观测地址<input v-model="form.address" placeholder="例如：浑南区高新路2号" /></label>
        <label>AQI预估等级<select v-model="form.estimate"><option v-for="item in levels" :key="item">{{item}}</option></select></label>
        <label class="wide">环境状况描述<textarea v-model="form.description" rows="4" placeholder="描述能见度、异味、扬尘等情况"></textarea></label>
      </div><button class="primary compact" @click="submit">提交反馈</button>
    </article>
    <article class="panel process-panel"><h2>闭环处理进度</h2><ol><li v-for="(item,i) in flow" :key="item"><b>{{i+1}}</b><div><strong>{{item}}</strong><span>{{flowDesc[i]}}</span></div></li></ol></article>
  </section>
  <section v-else-if="page==='history'" class="panel"><div class="panel-title"><div><h2>我的反馈记录</h2><p>共 {{filteredRows.length}} 条，可按编号、城市、状态和日期查询。</p></div><button class="secondary" @click="load"><i class="fa-solid fa-rotate"></i> 刷新</button></div>
    <div class="history-search"><label><i class="fa-solid fa-magnifying-glass"></i><input v-model="historyKeyword" placeholder="输入反馈编号、城市或地址"/></label><select v-model="historyStatus"><option value="">全部状态</option><option>待指派</option><option>已指派</option><option>已完成</option></select><input v-model="historyDate" type="date"/><button @click="historyKeyword='';historyStatus='';historyDate=''">重置</button></div>
    <el-table :data="pagedRows" stripe><el-table-column prop="feedbackNo" label="编号" min-width="165"/><el-table-column prop="province" label="省份"/><el-table-column prop="city" label="城市"/><el-table-column prop="address" label="地址" min-width="180"/><el-table-column label="预估AQI"><template #default="s"><AqiBadge :value="s.row.estimate"/></template></el-table-column><el-table-column prop="status" label="状态"/><el-table-column label="实测结果"><template #default="s"><AqiBadge :value="s.row.finalAqi"/></template></el-table-column><el-table-column label="详情" width="105"><template #default="s"><button class="detail-button" @click="openDetail(s.row)"><i class="fa-regular fa-eye"></i> 查看</button></template></el-table-column></el-table>
    <el-pagination class="table-pagination" v-model:current-page="historyPage" :page-size="6" layout="total, prev, pager, next, jumper" :total="filteredRows.length"/>
    <FeedbackDetailDialog v-model="detailVisible" :row="detailRow"/>
  </section>
  <template v-else><StatCards :items="profileStats"/><section class="panel profile-panel"><h2>账户资料</h2><dl><dt>真实姓名</dt><dd>{{session.name}}</dd><dt>脱敏手机号</dt><dd>{{session.phone}}</dd><dt>年龄</dt><dd>{{session.age}}</dd><dt>性别</dt><dd>{{session.gender}}</dd><dt>用户身份</dt><dd>NEPS公众监督员</dd><dt>数据权限</dt><dd>仅查看本人反馈</dd></dl></section></template>
</template>
<script setup>
import { computed, onMounted, reactive, ref } from 'vue'; import {ElMessage} from 'element-plus'; import http from '../api/http'; import AqiBadge from '../components/AqiBadge.vue'; import StatCards from '../components/StatCards.vue'; import ActivityTimeline from '../components/ActivityTimeline.vue'; import FeedbackDetailDialog from '../components/FeedbackDetailDialog.vue'
const props=defineProps({page:String,session:Object}); const rows=ref([]),historyKeyword=ref(''),historyStatus=ref(''),historyDate=ref(''),detailVisible=ref(false),detailRow=ref(null),historyPage=ref(1); const levels=['优','良','轻度污染','中度污染','重度污染','严重污染']; const cities={'辽宁省':['沈阳市','大连市','鞍山市','抚顺市'],'北京市':['北京市'],'上海市':['上海市'],'广东省':['广州市','深圳市','珠海市'],'四川省':['成都市','绵阳市']}; const form=reactive({supervisorId:props.session.id,province:'辽宁省',city:'沈阳市',address:'',estimate:'良',description:''}); const flow=['公众提交','管理员审核','网格员指派','现场实测','数据确认','决策统计']; const flowDesc=['生成唯一反馈编号','检查信息完整性','按省市网格匹配','录入SO2、CO、PM2.5','按最高等级计算AQI','汇总至可视化大屏'];
defineEmits(['navigate']); const guideTitle=['空气质量令人满意','空气质量可接受','敏感人群需注意','建议减少户外活动','应采取健康防护','避免户外活动']; const guideText=['适宜正常户外活动','极少数敏感人群注意','儿童老人减少长时活动','一般人群减少户外活动','停止户外运动并佩戴防护','尽量留在室内并关闭门窗']; const flowIcons=['fa-solid fa-pen','fa-solid fa-shield-halved','fa-solid fa-route','fa-solid fa-flask','fa-solid fa-circle-check','fa-solid fa-chart-line'];
const profileStats=computed(()=>[{label:'历史反馈',value:rows.value.length,icon:'fa-solid fa-message'},{label:'处理中',value:rows.value.filter(x=>x.status!=='已完成').length,icon:'fa-solid fa-spinner'},{label:'已完成',value:rows.value.filter(x=>x.status==='已完成').length,icon:'fa-solid fa-circle-check'},{label:'身份',value:'NEPS',icon:'fa-solid fa-user-shield'}]);
const filteredRows=computed(()=>rows.value.filter(item=>(!historyStatus.value||item.status===historyStatus.value)&&(!historyDate.value||String(item.createTime||'').startsWith(historyDate.value))&&(!historyKeyword.value||[item.feedbackNo,item.city,item.address].some(value=>String(value||'').includes(historyKeyword.value)))));function openDetail(row){detailRow.value=row;detailVisible.value=true}
const pagedRows=computed(()=>filteredRows.value.slice((historyPage.value-1)*6,historyPage.value*6))
async function load(){rows.value=(await http.get('/feedbacks',{params:{supervisorId:props.session.id}})).data} async function submit(){if(!form.address.trim()) return ElMessage.warning('请填写具体观测地址'); await http.post('/feedbacks',form); ElMessage.success('反馈已提交，正在等待管理员指派'); form.address='';form.description='';await load()} onMounted(load)
</script>
