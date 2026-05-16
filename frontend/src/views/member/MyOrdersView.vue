<script>
import MemberLayout from '@/layouts/MemberLayout'
import { getMyOrders } from '@/api/order'

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
  name: 'MyOrdersView',
  components: { MemberLayout },
  data() {
    return {
      orders: [],
      loading: false,
      error: '',
      expandedOrders: {},
    }
  },
  async created() {
    await this.loadOrders()
  },
  methods: {
    async loadOrders() {
      this.loading = true
      this.error = ''
      try {
        const res = await getMyOrders()
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

    toggleOrder(orderId) {
      this.expandedOrders[orderId] = !this.expandedOrders[orderId]
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
  },
}
</script>

<template>
  <MemberLayout>
    <div class="py-3">
      <h4 class="mb-4">我的訂單</h4>
      <BAlert v-if="error" variant="danger" :model-value="true">{{ error }}</BAlert>

      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status"></div>
      </div>

      <div v-else-if="orders.length === 0 && !error" class="text-center py-5 text-muted">
        <i class="bx bx-package fs-1 d-block mb-3"></i>
        <p>目前還沒有訂單記錄</p>
        <router-link to="/shop"><BButton variant="primary">去逛逛</BButton></router-link>
      </div>

      <div v-else>
        <BCard v-for="order in orders" :key="order.orderId" class="mb-3 shadow-sm">
          <BCardBody>
            <div class="d-flex flex-wrap justify-content-between align-items-start gap-2">
              <div>
                <div class="fw-bold">訂單編號：{{ order.orderId }}</div>
                <div class="text-muted small">{{ formatDate(order.inptime) }}</div>
              </div>
              <div class="d-flex gap-2 align-items-center flex-wrap">
                <BBadge :variant="payStatusVariant(order.payStatus)">{{ payStatusLabel(order.payStatus) }}</BBadge>
                <BBadge :variant="orderStatusVariant(order.orderStatus)">{{ orderStatusLabel(order.orderStatus) }}</BBadge>
                <span class="fw-bold text-primary">{{ formatPrice(order.totalPrice) }}</span>
                <BButton size="sm" variant="outline-secondary" @click="toggleOrder(order.orderId)">
                  {{ expandedOrders[order.orderId] ? '收起' : '查看明細' }}
                </BButton>
              </div>
            </div>

            <!-- Order Details -->
            <div v-if="expandedOrders[order.orderId]" class="mt-3 pt-3 border-top">
              <div v-if="order.remark" class="text-muted small mb-2">備註：{{ order.remark }}</div>
              <table class="table table-sm table-hover mb-0">
                <thead>
                  <tr>
                    <th>商品名稱</th>
                    <th class="text-end">單價</th>
                    <th class="text-center">數量</th>
                    <th class="text-end">小計</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="detail in (order.details || [])" :key="detail.orderItemSn">
                    <td>{{ detail.productName }}</td>
                    <td class="text-end">{{ formatPrice(detail.standPrice) }}</td>
                    <td class="text-center">{{ detail.quantity }}</td>
                    <td class="text-end">{{ formatPrice(calcItemTotal(detail.standPrice, detail.quantity)) }}</td>
                  </tr>
                  <tr v-if="!order.details || order.details.length === 0">
                    <td colspan="4" class="text-center text-muted">無明細資料</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </BCardBody>
        </BCard>
      </div>
    </div>
  </MemberLayout>
</template>
