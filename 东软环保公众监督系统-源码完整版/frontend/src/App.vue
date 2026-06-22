<template>
  <LoginPortal v-if="!session" @login="session = $event" />
  <RoleWorkspace v-else :session="session" @logout="session = null" />
</template>

<script setup>
import { ref } from 'vue'
import LoginPortal from './components/LoginPortal.vue'
import RoleWorkspace from './components/RoleWorkspace.vue'

const session = ref(null)
</script>
