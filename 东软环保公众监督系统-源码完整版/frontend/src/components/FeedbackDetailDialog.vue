<template>
  <el-dialog :model-value="modelValue" width="720px" title="监督反馈详细信息" @close="$emit('update:modelValue',false)">
    <template v-if="row">
      <div class="detail-heading"><div><span>反馈编号</span><strong>{{row.feedbackNo}}</strong></div><em :class="statusClass(row.status)">{{row.status}}</em></div>
      <dl class="detail-grid"><dt>所属区域</dt><dd>{{row.province}} · {{row.city}}</dd><dt>观测地址</dt><dd>{{row.address}}</dd><dt>公众预估</dt><dd><AqiBadge :value="row.estimate"/></dd><dt>提交时间</dt><dd>{{format(row.createTime)}}</dd><dt>情况描述</dt><dd class="detail-wide">{{row.description||'未填写补充描述'}}</dd><dt>指派类型</dt><dd>{{row.assignType||'待指派'}}</dd><dt>检测网格员</dt><dd>{{row.workerName||'尚未指派'}}</dd><dt>指派时间</dt><dd>{{format(row.assignTime)}}</dd></dl>
      <section class="measurement-detail"><h3>现场实测数据</h3><div><article><span>SO2 二氧化硫</span><AqiBadge :value="row.so2"/></article><article><span>CO 一氧化碳</span><AqiBadge :value="row.co"/></article><article><span>PM2.5 细颗粒物</span><AqiBadge :value="row.pm25"/></article><article class="final-measure"><span>最终AQI</span><AqiBadge :value="row.finalAqi"/></article></div><p>计算规则：最终AQI = MAX（SO2，CO，PM2.5）</p></section>
      <section class="detail-progress"><h3>业务流转进度</h3><div><span v-for="(step,index) in steps" :key="step" :class="{complete:index<=progressIndex}"><i :class="index<progressIndex?'fa-solid fa-check':'fa-solid fa-circle' "></i><b>{{step}}</b></span></div></section>
    </template>
    <template #footer><button class="secondary" @click="$emit('update:modelValue',false)">关闭</button></template>
  </el-dialog>
</template>
<script setup>
import {computed} from 'vue';import AqiBadge from './AqiBadge.vue'
const props=defineProps({modelValue:Boolean,row:Object});defineEmits(['update:modelValue'])
const steps=['公众上报','信息审核','任务指派','现场实测','数据归档'];
const progressIndex=computed(()=>props.row?.status==='已完成'?4:props.row?.status==='已指派'?2:1)
const format=value=>value?String(value).replace('T',' ').slice(0,19):'-'
const statusClass=status=>status==='已完成'?'done-status':status==='已指派'?'doing-status':'waiting-status'
</script>
