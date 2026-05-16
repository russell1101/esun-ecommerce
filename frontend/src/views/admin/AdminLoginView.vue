<script>
import Layout from '@/layouts/auth'
import { adminLogin } from '@/api/auth'
import { useAuthStore } from '@/state/pinia'

export default {
  components: { Layout },
  data() {
    return {
      form: { username: '', password: '' },
      loading: false,
      error: '',
    }
  },
  methods: {
    async handleLogin() {
      this.error = ''
      if (!this.form.username || !this.form.password) {
        this.error = '請輸入帳號和密碼'
        return
      }
      this.loading = true
      try {
        const res = await adminLogin(this.form)
        if (res.success === 1) {
          useAuthStore().setAuth(res.token, res.data)
          this.$router.push('/admin/products')
        } else {
          this.error = res.errMsg || '登入失敗'
        }
      } catch (e) {
        this.error = e?.response?.data?.errMsg || '系統錯誤，請稍後再試'
      } finally {
        this.loading = false
      }
    },
  },
}
</script>

<template>
  <Layout>
    <BRow class="justify-content-center">
      <BCol md="8" lg="6" xl="5">
        <BCard no-body class="overflow-hidden">
          <div class="bg-danger-subtle">
            <BRow>
              <BCol cols="7">
                <div class="text-danger p-4">
                  <h5 class="text-danger">管理員後台</h5>
                  <p>請使用管理員帳號登入</p>
                </div>
              </BCol>
              <BCol cols="5" class="align-self-end">
                <img src="@/assets/images/profile-img.png" alt class="img-fluid" />
              </BCol>
            </BRow>
          </div>
          <BCardBody class="pt-0">
            <div>
              <router-link to="/">
                <div class="avatar-md profile-user-wid mb-4">
                  <span class="avatar-title rounded-circle bg-light">
                    <img src="@/assets/images/logo.svg" alt height="34" />
                  </span>
                </div>
              </router-link>
            </div>
            <BAlert v-if="error" variant="danger" :model-value="true">{{ error }}</BAlert>
            <BForm class="p-2" @submit.prevent="handleLogin">
              <BFormGroup class="mb-3" label="管理員帳號" label-for="username">
                <BFormInput id="username" v-model="form.username" type="text" placeholder="請輸入管理員帳號" />
              </BFormGroup>
              <BFormGroup class="mb-3" label="密碼" label-for="password">
                <BFormInput id="password" v-model="form.password" type="password" placeholder="請輸入密碼" />
              </BFormGroup>
              <div class="mt-3 d-grid">
                <BButton type="submit" variant="danger" :disabled="loading">
                  {{ loading ? '登入中...' : '管理員登入' }}
                </BButton>
              </div>
            </BForm>
            <div class="mt-3 text-center">
              <p class="mb-0"><router-link to="/login" class="text-muted small">返回會員登入</router-link></p>
            </div>
          </BCardBody>
        </BCard>
      </BCol>
    </BRow>
  </Layout>
</template>
