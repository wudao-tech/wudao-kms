<template>
    <div v-loading.fullscreen="pageLoading">
        
    </div>
</template>
<script setup>
import { useRouter } from 'vue-router';
import { useUserStore } from '@/store/modules/user';
import { ElLoading } from 'element-plus'
import { to } from 'await-to-js'

const router = useRouter();
const userStore = useUserStore();
const pageLoading = ref(false)
onMounted(async () => {
    pageLoading.value = true
    const loading = ElLoading.service({
        lock: true,
        text: '登录中...',
        // background: 'rgba(0, 0, 0, 0.5)',
    })
    let params = router.currentRoute.value.query
    if (params.source) {
        const [err, res] = await to(userStore.loginSSO(params))
        if (res) {
            localStorage.removeItem('route_tab')
            const redirectUrl = '/'
            await router.push(redirectUrl)
        }
    }
    setTimeout(() => {
        loading.close()
        pageLoading.value = false
    }, 1000)

});

</script>
<style scoped>

</style>