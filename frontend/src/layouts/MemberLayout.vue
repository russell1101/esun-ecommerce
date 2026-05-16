<script>
import { useAuthStore } from '@/state/pinia'

export default {
  name: 'MemberLayout',
  computed: {
    auth() { return useAuthStore() },
    username() { return this.auth.username },
    isLoggedIn() { return this.auth.isLoggedIn },
  },
  methods: {
    logout() {
      this.auth.logout()
      this.$router.push('/login')
    },
  },
}
</script>

<template>
  <div>
    <!-- Topbar -->
    <header id="page-topbar">
      <div class="navbar-header">
        <div class="d-flex align-items-center">
          <div class="navbar-brand-box">
            <router-link to="/shop" class="logo logo-dark">
              <span class="logo-sm">
                <img src="@/assets/images/logo.svg" alt height="22" />
              </span>
              <span class="logo-lg">
                <img src="@/assets/images/logo-dark.png" alt height="17" />
              </span>
            </router-link>
          </div>
          <!-- nav links -->
          <nav class="d-none d-md-flex align-items-center gap-3 ms-3">
            <router-link to="/shop" class="nav-link text-dark fw-medium">商品列表</router-link>
            <router-link v-if="isLoggedIn" to="/orders" class="nav-link text-dark fw-medium">我的訂單</router-link>
          </nav>
        </div>

        <div class="d-flex align-items-center gap-2">
          <template v-if="isLoggedIn">
            <span class="d-none d-sm-inline text-muted me-1">{{ username }}</span>
            <BButton size="sm" variant="outline-danger" @click="logout">登出</BButton>
          </template>
          <template v-else>
            <router-link to="/login">
              <BButton size="sm" variant="outline-primary">登入</BButton>
            </router-link>
            <router-link to="/register">
              <BButton size="sm" variant="primary">註冊</BButton>
            </router-link>
          </template>
        </div>
      </div>
    </header>

    <!-- Main content -->
    <div class="main-content">
      <div class="page-content">
        <BContainer fluid>
          <slot />
        </BContainer>
      </div>
    </div>
  </div>
</template>
