<script>
import { useAuthStore, useLayoutStore } from '@/state/pinia'

/**
 * Nav-bar Component
 */
export default {
  data() {
    return {};
  },
  components: {},
  computed: {
    auth() {
      return useAuthStore()
    },
    username() {
      return this.auth.username || 'Admin'
    },
    layout() {
      return useLayoutStore();
    }
  },
  mounted() {
    document.body.setAttribute("data-bs-theme", this.layout.mode);
  },
  methods: {
    toggleMenu() {
      this.$parent.toggleMenu();
    },
    toggleRightSidebar() {
      this.$parent.toggleRightSidebar();
    },
    initFullScreen() {
      document.body.classList.toggle("fullscreen-enable");
      if (
        !document.fullscreenElement &&
        !document.mozFullScreenElement &&
        !document.webkitFullscreenElement
      ) {
        if (document.documentElement.requestFullscreen) {
          document.documentElement.requestFullscreen();
        } else if (document.documentElement.mozRequestFullScreen) {
          document.documentElement.mozRequestFullScreen();
        } else if (document.documentElement.webkitRequestFullscreen) {
          document.documentElement.webkitRequestFullscreen(
            Element.ALLOW_KEYBOARD_INPUT
          );
        }
      } else {
        if (document.cancelFullScreen) {
          document.cancelFullScreen();
        } else if (document.mozCancelFullScreen) {
          document.mozCancelFullScreen();
        } else if (document.webkitCancelFullScreen) {
          document.webkitCancelFullScreen();
        }
      }
    },
    toggleTheme() {
      const newMode = this.layout.mode === "dark" ? "light" : "dark";
      this.layout.changeMode(newMode);
      document.body.setAttribute("data-bs-theme", newMode);
      sessionStorage.setItem("theme", newMode);
    },
    logoutUser() {
      this.auth.logout()
      this.$router.push('/admin/login')
    },
  },
};
</script>

<template>
  <header id="page-topbar">
    <div class="navbar-header">
      <div class="d-flex">
        <!-- LOGO -->
        <div class="navbar-brand-box">
          <router-link to="/admin/products" class="logo logo-dark">
            <span class="logo-sm">
              <img src="@/assets/images/logo.svg" alt height="22" />
            </span>
            <span class="logo-lg">
              <img src="@/assets/images/logo-dark.png" alt height="17" />
            </span>
          </router-link>

          <router-link to="/admin/products" class="logo logo-light">
            <span class="logo-sm">
              <img src="@/assets/images/logo-light.svg" alt height="22" />
            </span>
            <span class="logo-lg">
              <img src="@/assets/images/logo-light.png" alt height="19" />
            </span>
          </router-link>
        </div>

        <BButton variant="white" id="vertical-menu-btn" type="button" class="btn btn-sm px-3 font-size-16 header-item"
          @click="toggleMenu">
          <i class="fa fa-fw fa-bars"></i>
        </BButton>
      </div>

      <div class="d-flex align-items-center">
        <!-- Full Screen -->
        <div class="dropdown d-none d-lg-inline-block ms-1">
          <BButton variant="white" type="button" class="btn header-item noti-icon" @click="initFullScreen">
            <i class="bx bx-fullscreen"></i>
          </BButton>
        </div>

        <!-- Light/Dark Toggle -->
        <div class="d-none d-lg-inline-block light-dark-mode ms-1">
          <BButton variant="link" class="header-item noti-icon waves-effect" @click="toggleTheme">
            <i :class="layout.mode === 'dark' ? 'bx bx-sun' : 'bx bx-moon'"></i>
          </BButton>
        </div>

        <!-- User Dropdown -->
        <BDropdown right variant="black" toggle-class="header-item" menu-class="dropdown-menu-end">
          <template v-slot:button-content>
            <i class="bx bx-user-circle font-size-20 align-middle me-1"></i>
            <span class="d-none d-xl-inline-block ms-1">{{ username }}</span>
            <i class="mdi mdi-chevron-down d-none d-xl-inline-block"></i>
          </template>
          <BDropdownDivider></BDropdownDivider>
          <a href="javascript:void(0);" class="dropdown-item text-danger" @click="logoutUser">
            <i class="bx bx-power-off font-size-16 align-middle me-1 text-danger"></i>
            登出
          </a>
        </BDropdown>

      </div>
    </div>
  </header>
</template>
