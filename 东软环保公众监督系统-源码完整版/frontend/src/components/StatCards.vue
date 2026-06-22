<template><section class="stat-grid"><article v-for="item in items" :key="item.label"><i :class="item.icon"></i><div><span>{{ item.label }}</span><strong>{{ item.value ?? 0 }}</strong></div></article></section></template>
<script setup>defineProps({items:Array})</script>
