<template><article class="panel activity-panel"><div class="panel-title"><div><h2>最新业务动态</h2><p>公众反馈从上报到实测的实时流转</p></div></div><ol><li v-for="item in items.slice(0,6)" :key="item.id"><i :class="icon(item.status)"></i><div><strong>{{text(item)}}</strong><span>{{item.feedbackNo}} · {{item.province}}{{item.city}} · {{formatTime(item.createTime)}}</span></div><em :class="statusClass(item.status)">{{item.status}}</em></li></ol></article></template>
<script setup>
defineProps({items:{type:Array,default:()=>[]}})
const icon=status=>status==='已完成'?'fa-solid fa-circle-check':status==='已指派'?'fa-solid fa-route':'fa-solid fa-bell'
const text=item=>item.status==='已完成'?`${item.workerName||'网格员'}已提交实测结果`:item.status==='已指派'?`任务已指派给${item.workerName||'网格员'}`:'收到新的公众监督反馈'
const statusClass=status=>status==='已完成'?'done-status':status==='已指派'?'doing-status':'waiting-status'
const formatTime=value=>value?String(value).replace('T',' ').slice(0,16):'刚刚'
</script>
