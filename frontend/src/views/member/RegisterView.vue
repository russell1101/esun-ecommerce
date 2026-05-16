<script>
import Layout from '@/layouts/auth'
import { memberRegister } from '@/api/auth'

export default {
  components: { Layout },
  data() {
    return {
      form: { username: '', password: '', email: '', phone: '' },
      loading: false,
      error: '',
      success: '',
    }
  },
  methods: {
    async handleRegister() {
      this.error = ''
      this.success = ''
      if (!this.form.username || !this.form.password) {
        this.error = '請輸入帳號和密碼'
        return
      }
      this.loading = true
      try {
        const res = await memberRegister(this.form)
        if (res.success === 1) {
          this.success = '註冊成功！即將跳轉到登入頁...'
          setTimeout(() => this.$router.push('/login'), 1500)
        } else {
          this.error = res.errMsg || '註冊失敗'
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
          <div class="bg-primary-subtle">
            <BRow>
              <BCol cols="7">
                <div class="text-primary p-4">
                  <h5 class="text-primary">立即加入！</h5>
                  <p>建立您的玉山電商帳號</p>
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
            <BAlert v-if="success" variant="success" :model-value="true">{{ success }}</BAlert>
            <BForm class="p-2" @submit.prevent="handleRegister">
              <BFormGroup class="mb-3" label="帳號 *" label-for="username">
                <BFormInput id="username" v-model="form.username" type="text" placeholder="請輸入帳號" required />
              </BFormGroup>
              <BFormGroup class="mb-3" label="密碼 *" label-for="password">
                <BFormInput id="password" v-model="form.password" type="password" placeholder="請輸入密碼" required />
              </BFormGroup>
              <BFormGroup class="mb-3" label="Email（選填）" label-for="email">
                <BFormInput id="email" v-model="form.email" type="email" placeholder="請輸入 Email" />
              </BFormGroup>
              <BFormGroup class="mb-3" label="手機號碼（選填）" label-for="phone">
                <BFormInput id="phone" v-model="form.phone" type="text" placeholder="請輸入手機號碼" />
              </BFormGroup>
              <div class="mt-3 d-grid">
                <BButton type="submit" variant="primary" :disabled="loading">
                  {{ loading ? '註冊中...' : '立即註冊' }}
                </BButton>
              </div>
            </BForm>
            <div class="mt-3 text-center">
              <p>已有帳號？<router-link to="/login" class="fw-medium text-primary">立即登入</router-link></p>
            </div>
          </BCardBody>
        </BCard>
      </BCol>
    </BRow>
  </Layout>
</template>
