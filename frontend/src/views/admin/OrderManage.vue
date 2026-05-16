<script>
import Layout from '@/layouts/vertical'
import { getAdminOrders, getOrderDetails } from '@/api/order'
import { useAuthStore } from '@/state/pinia'

const ORDER_STATUS_MAP = {
  1: '已建立',
  2: '處理中',
  3: '已完成',
  9: '已取消',
}

const PAY_STATUS_MAP = {
  0: '未付款',
  1: '已付款',
}

export default {
  name: 'OrderManage',
  components: { Layout },
  data() {
    return {
      orders: [],
      loading: false,
      error: '',

      // Detail Modal
      showDetailModal: false,
      currentOrder: null,
      currentDetails: [],
      detailLoading: false,
      detailError: '',
    }
  },
  computed: {
    auth() { return useAuthStore() },
    username() { return this.auth.username },
  },
  async created() {
    await this.loadOrders()
  },
  methods: {
    async loadOrders() {
      this.loading = true
      this.error = ''
      try {
        const res = await getAdminOrders()
        if (res.success === 1) {
          this.orders = res.data || []
        } else {
          this.error = res.errMsg || '載入訂單失敗'
        }
      } catch (e) {
        this.error = e?.response?.data?.errMsg || '系統錯誤，請稍後再試'
      } finally {
        this.loading = false
      }
    },

    async openDetailModal(order) {
      this.currentOrder = order
      this.currentDetails = []
      this.detailError = ''
      this.showDetailModal = true
      this.detailLoading = true
      try {
        const res = await getOrderDetails(order.orderId)
        if (res.success === 1) {
          this.currentDetails = res.data || []
        } else {
          this.detailError = res.errMsg || '載入明細失敗'
        }
      } catch (e) {
        this.detailError = e?.response?.data?.errMsg || '系統錯誤，請稍後再試'
      } finally {
        this.detailLoading = false
      }
    },

    formatPrice(val) {
      return 'NT$ ' + Number(val).toLocaleString('zh-TW', { minimumFractionDigits: 2 })
    },

    formatDate(dateStr) {
      if (!dateStr) return '-'
      const d = new Date(dateStr)
      const pad = n => String(n).padStart(2, '0')
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
    },

    orderStatusLabel(status) {
      return ORDER_STATUS_MAP[status] || `狀態${status}`
    },

    orderStatusVariant(status) {
      return { 1: 'secondary', 2: 'warning', 3: 'success', 9: 'danger' }[status] || 'secondary'
    },

    payStatusLabel(status) {
      return PAY_STATUS_MAP[status] || '-'
    },

    payStatusVariant(status) {
      return status === 1 ? 'success' : 'warning'
    },

    calcItemTotal(price, quantity) {
      return (Math.round(price * 100) * quantity) / 100
    },

    logout() {
      this.auth.logout()
      this.$router.push('/admin/login')
    },
  },
}
</script>

<template>
  <Layout>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h4 class="mb-0">訂單管理</h4>
    </div>

    <BAlert v-if="error" variant="danger" :model-value="true">{{ error }}</BAlert>

    <BCard class="shadow-sm">
      <BCardBody class="p-0">
        <div v-if="loading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status"></div>
        </div>
        <div v-else class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
              <tr>
                <th>訂單編號</th>
                <th>會員帳號</th>
                <th>建立時間</th>
                <th>總金額</th>
                <th>付款狀態</th>
                <th>訂單狀態</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in orders" :key="order.orderId">
                <td>{{ order.orderId }}</td>
                <td>{{ order.memberUsername }}</td>
                <td>{{ formatDate(order.inptime) }}</td>
                <td>{{ formatPrice(order.totalPrice) }}</td>
                <td>
                  <BBadge :variant="payStatusVariant(order.payStatus)">{{ payStatusLabel(order.payStatus) }}</BBadge>
                </td>
                <td>
                  <BBadge :variant="orderStatusVariant(order.orderStatus)">{{ orderStatusLabel(order.orderStatus) }}</BBadge>
                </td>
                <td>
                  <BButton size="sm" variant="outline-info" @click="openDetailModal(order)">
                    <i class="bx bx-list-ul me-1"></i>查看明細
                  </BButton>
                </td>
              </tr>
              <tr v-if="orders.length === 0">
                <td colspan="7" class="text-center text-muted py-4">目前沒有訂單資料</td>
              </tr>
            </tbody>
          </table>
        </div>
      </BCardBody>
    </BCard>

    <!-- Order Detail Modal -->
    <BModal v-model="showDetailModal" size="lg" hide-footer
      :title="currentOrder ? `訂單明細 - ${currentOrder.orderId}` : '訂單明細'">
      <BAlert v-if="detailError" variant="danger" :model-value="true">{{ detailError }}</BAlert>

      <div v-if="currentOrder" class="mb-3 text-muted small">
        <span class="me-3">會員：{{ currentOrder.memberUsername }}</span>
        <span class="me-3">時間：{{ formatDate(currentOrder.inptime) }}</span>
        <span>備註：{{ currentOrder.remark || '無' }}</span>
      </div>

      <div v-if="detailLoading" class="text-center py-4">
        <div class="spinner-border text-primary" role="status"></div>
      </div>
      <div v-else>
        <table class="table table-sm table-hover">
          <thead>
            <tr>
              <th>商品名稱</th>
              <th class="text-end">單價</th>
              <th class="text-center">數量</th>
              <th class="text-end">小計</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="detail in currentDetails" :key="detail.orderItemSn">
              <td>{{ detail.productName }}</td>
              <td class="text-end">{{ formatPrice(detail.standPrice) }}</td>
              <td class="text-center">{{ detail.quantity }}</td>
              <td class="text-end">{{ formatPrice(calcItemTotal(detail.standPrice, detail.quantity)) }}</td>
            </tr>
            <tr v-if="currentDetails.length === 0">
              <td colspan="4" class="text-center text-muted">無明細資料</td>
            </tr>
          </tbody>
          <tfoot v-if="currentOrder && currentDetails.length > 0">
            <tr class="fw-bold">
              <td colspan="3" class="text-end">總計</td>
              <td class="text-end text-primary">{{ formatPrice(currentOrder.totalPrice) }}</td>
            </tr>
          </tfoot>
        </table>
      </div>
    </BModal>
  </Layout>
</template>
