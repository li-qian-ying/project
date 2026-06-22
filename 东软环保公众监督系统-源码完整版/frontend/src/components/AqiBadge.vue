<template><span class="aqi-badge" :class="`level-${index}`">{{ value || '-' }}</span></template>
<script setup>
import { computed } from 'vue'
const props=defineProps({value:String}); const levels=['优','良','轻度污染','中度污染','重度污染','严重污染']; const index=computed(()=>Math.max(0,levels.indexOf(props.value)))
</script>
