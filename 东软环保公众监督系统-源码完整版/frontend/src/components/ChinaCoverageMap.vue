<template>
  <article class="panel china-map-panel">
    <div class="panel-title"><div><h2>公众监督区域地图</h2><p>地图气泡大小表示各省反馈数量</p></div><span class="data-badge">全国网格</span></div>
    <div class="map-stage">
      <svg viewBox="0 0 760 430" role="img" aria-label="公众监督区域分布地图">
        <path class="china-outline" d="M111 142 L168 102 L229 89 L279 53 L346 65 L394 45 L448 65 L498 91 L553 91 L578 119 L634 139 L663 183 L643 218 L674 255 L641 285 L605 283 L575 324 L528 328 L494 367 L443 353 L411 385 L356 368 L321 391 L270 369 L226 378 L192 347 L146 342 L128 304 L92 278 L104 237 L75 204 L93 171 Z"/>
        <path class="map-boundary" d="M168 102 L192 177 L146 242 M229 89 L248 164 L192 177 L226 260 M279 53 L305 132 L248 164 L321 227 M346 65 L360 145 L305 132 M394 45 L421 125 L360 145 L411 219 M448 65 L462 143 L421 125 M498 91 L505 165 L462 143 L535 225 M553 91 L548 155 L634 139 M128 304 L226 260 L270 369 M226 260 L321 227 L356 368 M321 227 L411 219 L443 353 M411 219 L535 225 L494 367 M535 225 L605 283"/>
        <g v-for="item in markers" :key="item.name" class="map-marker">
          <circle :cx="item.x" :cy="item.y" :r="item.radius+8" class="marker-halo"/>
          <circle :cx="item.x" :cy="item.y" :r="item.radius" class="marker-core"><title>{{item.name}}：{{item.value}}条反馈</title></circle>
          <text :x="item.x" :y="item.y-18" text-anchor="middle">{{item.name}}</text><text :x="item.x" :y="item.y+4" text-anchor="middle" class="marker-value">{{item.value}}</text>
        </g>
      </svg>
      <div class="map-legend"><span><i class="low"></i>低热度</span><span><i class="medium"></i>中热度</span><span><i class="high"></i>高热度</span></div>
    </div>
  </article>
</template>
<script setup>
import{computed}from'vue';const props=defineProps({provinces:{type:Object,default:()=>({})}})
const positions={'辽宁省':[548,132],'北京市':[493,157],'河北省':[465,178],'山东省':[522,198],'江苏省':[542,232],'上海市':[573,250],'浙江省':[548,273],'湖北省':[430,250],'广东省':[431,333],'四川省':[307,255],'陕西省':[368,214]}
const markers=computed(()=>Object.entries(props.provinces).map(([name,value])=>{const pos=positions[name]||[380,220];return{name,value,x:pos[0],y:pos[1],radius:Math.min(19,9+Number(value)*2)}}))
</script>
