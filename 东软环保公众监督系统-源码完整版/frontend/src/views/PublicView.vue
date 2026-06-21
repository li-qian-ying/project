<template>
  <section v-if="page==='submit'" class="content-grid">
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
  <section v-else-if="page==='history'" class="panel"><div class="panel-title"><div><h2>我的反馈记录</h2><p>共 {{rows.length}} 条，可追踪指派和实测结果。</p></div><button class="secondary" @click="load">刷新</button></div>
    <el-table :data="rows" stripe><el-table-column prop="feedbackNo" label="编号" min-width="165"/><el-table-column prop="province" label="省份"/><el-table-column prop="city" label="城市"/><el-table-column prop="address" label="地址" min-width="180"/><el-table-column label="预估AQI"><template #default="s"><AqiBadge :value="s.row.estimate"/></template></el-table-column><el-table-column prop="status" label="状态"/><el-table-column label="实测结果"><template #default="s"><AqiBadge :value="s.row.finalAqi"/></template></el-table-column></el-table>
  </section>
  <template v-else><StatCards :items="profileStats"/><section class="panel profile-panel"><h2>账户资料</h2><dl><dt>真实姓名</dt><dd>{{session.name}}</dd><dt>脱敏手机号</dt><dd>{{session.phone}}</dd><dt>年龄</dt><dd>{{session.age}}</dd><dt>性别</dt><dd>{{session.gender}}</dd><dt>用户身份</dt><dd>NEPS公众监督员</dd><dt>数据权限</dt><dd>仅查看本人反馈</dd></dl></section></template>
</template>
<script setup>
import { computed, onMounted, reactive, ref } from 'vue'; import {ElMessage} from 'element-plus'; import http from '../api/http'; import AqiBadge from '../components/AqiBadge.vue'; import StatCards from '../components/StatCards.vue'
const props=defineProps({page:String,session:Object}); const rows=ref([]); const levels=['优','良','轻度污染','中度污染','重度污染','严重污染']; const cities={'辽宁省':['沈阳市','大连市','鞍山市','抚顺市'],'北京市':['北京市'],'上海市':['上海市'],'广东省':['广州市','深圳市','珠海市'],'四川省':['成都市','绵阳市']}; const form=reactive({supervisorId:props.session.id,province:'辽宁省',city:'沈阳市',address:'',estimate:'良',description:''}); const flow=['公众提交','管理员审核','网格员指派','现场实测','数据确认','决策统计']; const flowDesc=['生成唯一反馈编号','检查信息完整性','按省市网格匹配','录入SO2、CO、PM2.5','按最高等级计算AQI','汇总至可视化大屏'];
const profileStats=computed(()=>[{label:'历史反馈',value:rows.value.length,icon:'fa-solid fa-message'},{label:'处理中',value:rows.value.filter(x=>x.status!=='已完成').length,icon:'fa-solid fa-spinner'},{label:'已完成',value:rows.value.filter(x=>x.status==='已完成').length,icon:'fa-solid fa-circle-check'},{label:'身份',value:'NEPS',icon:'fa-solid fa-user-shield'}]);
async function load(){rows.value=(await http.get('/feedbacks',{params:{supervisorId:props.session.id}})).data} async function submit(){if(!form.address.trim()) return ElMessage.warning('请填写具体观测地址'); await http.post('/feedbacks',form); ElMessage.success('反馈已提交，正在等待管理员指派'); form.address='';form.description='';await load()} onMounted(load)
</script>
