<script>
import MemberLayout from '@/layouts/MemberLayout'
import { getAvailableProducts } from '@/api/product'
import { createOrder } from '@/api/order'
import { useAuthStore } from '@/state/pinia'

export default {
  name: 'ShopView',
  components: { MemberLayout },
  data() {
    return {
      products: [],
      quantities: {},
      cart: [],
      remark: '',
      loading: false,
      checkoutLoading: false,
      error: '',
      cartError: '',
      cartSuccess: '',
    }
  },
  computed: {
    cartTotal() {
      return this.calcCartTotal()
    },
  },
  async created() {
    await this.loadProducts()
  },
  methods: {
    async loadProducts() {
      this.loading = true
      this.error = ''
      try {
        const res = await getAvailableProducts()
        if (res.success === 1) {
          this.products = res.data || []
          this.products.forEach(p => {
            this.quantities[p.productId] = 1
          })
        } else {
          this.error = res.errMsg || '載入商品失敗'
        }
      } catch (e) {
        this.error = e?.response?.data?.errMsg || '系統錯誤，請稍後再試'
      } finally {
        this.loading = false
      }
    },

    addToCart(product) {
      const qty = this.quantities[product.productId] || 1
      const existing = this.cart.find(item => item.productId === product.productId)
      const currentInCart = existing ? existing.quantity : 0
      const newTotal = currentInCart + qty

      if (newTotal > product.quantity) {
        this.cartError = `「${product.productName}」庫存僅剩 ${product.quantity} 件，購物車已有 ${currentInCart} 件，最多只能再加 ${product.quantity - currentInCart} 件`
        return
      }

      if (existing) {
        existing.quantity = newTotal
      } else {
        this.cart.push({
          productId: product.productId,
          productName: product.productName,
          price: product.price,
          quantity: qty,
        })
      }
      this.cartSuccess = `已加入「${product.productName}」到購物車`
      setTimeout(() => { this.cartSuccess = '' }, 2000)
    },

    removeFromCart(productId) {
      this.cart = this.cart.filter(item => item.productId !== productId)
    },

    calcItemTotal(price, quantity) {
      return (Math.round(price * 100) * quantity) / 100
    },

    calcCartTotal() {
      const cents = this.cart.reduce((sum, item) => {
        return sum + Math.round(item.price * 100) * item.quantity
      }, 0)
      return cents / 100
    },

    formatPrice(val) {
      return 'NT$ ' + Number(val).toLocaleString('zh-TW', { minimumFractionDigits: 2 })
    },

    async checkout() {
      this.cartError = ''
      const auth = useAuthStore()
      if (!auth.isMember) {
        this.cartError = '請先登入會員才能結帳'
        setTimeout(() => this.$router.push('/login'), 1500)
        return
      }
      if (this.cart.length === 0) {
        this.cartError = '購物車是空的'
        return
      }
      this.checkoutLoading = true
      try {
        const payload = {
          items: this.cart.map(item => ({
            productId: item.productId,
            quantity: item.quantity,
          })),
          remark: this.remark,
        }
        const res = await createOrder(payload)
        if (res.success === 1) {
          this.cartSuccess = `訂單建立成功！訂單編號：${res.data || ''}`
          this.cart = []
          this.remark = ''
        } else {
          this.cartError = res.errMsg || '結帳失敗'
        }
      } catch (e) {
        this.cartError = e?.response?.data?.errMsg || '系統錯誤，請稍後再試'
      } finally {
        this.checkoutLoading = false
      }
    },
  },
}
</script>

<template>
  <MemberLayout>
    <div class="py-3">
      <h4 class="mb-4">商品列表</h4>
      <BAlert v-if="error" variant="danger" :model-value="true">{{ error }}</BAlert>
      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status"></div>
      </div>

      <BRow v-else>
        <!-- Product List -->
        <BCol lg="8">
          <BRow>
            <BCol v-for="product in products" :key="product.productId" md="6" class="mb-4">
              <BCard class="h-100 shadow-sm">
                <BCardBody>
                  <h5 class="card-title">{{ product.productName }}</h5>
                  <p class="text-primary fw-bold fs-5 mb-1">{{ formatPrice(product.price) }}</p>
                  <p class="text-muted small mb-3">庫存：{{ product.quantity }} 件</p>
                  <div class="d-flex align-items-center gap-2">
                    <div class="input-group" style="width: 120px;">
                      <button class="btn btn-outline-secondary btn-sm" type="button"
                        @click="quantities[product.productId] = Math.max(1, (quantities[product.productId] || 1) - 1)">
                        -
                      </button>
                      <input type="number" class="form-control form-control-sm text-center"
                        v-model.number="quantities[product.productId]"
                        min="1" :max="product.quantity" />
                      <button class="btn btn-outline-secondary btn-sm" type="button"
                        @click="quantities[product.productId] = Math.min(product.quantity, (quantities[product.productId] || 1) + 1)">
                        +
                      </button>
                    </div>
                    <BButton variant="primary" size="sm" @click="addToCart(product)"
                      :disabled="product.quantity === 0">
                      {{ product.quantity === 0 ? '已售完' : '加入購物車' }}
                    </BButton>
                  </div>
                </BCardBody>
              </BCard>
            </BCol>
            <BCol cols="12" v-if="products.length === 0 && !loading">
              <div class="text-center py-5 text-muted">目前沒有上架商品</div>
            </BCol>
          </BRow>
        </BCol>

        <!-- Cart Panel -->
        <BCol lg="4">
          <BCard class="shadow-sm sticky-top" style="top: 80px;">
            <BCardHeader><strong>購物車</strong></BCardHeader>
            <BCardBody>
              <div v-if="cartError" class="alert alert-danger alert-dismissible fade show" role="alert">
                {{ cartError }}
                <button type="button" class="btn-close" @click="cartError = ''"></button>
              </div>
              <BAlert v-if="cartSuccess" variant="success" :model-value="true">{{ cartSuccess }}</BAlert>

              <div v-if="cart.length === 0" class="text-muted text-center py-3">購物車是空的</div>
              <div v-else>
                <div v-for="item in cart" :key="item.productId"
                  class="d-flex justify-content-between align-items-center mb-2 pb-2 border-bottom">
                  <div>
                    <div class="fw-medium">{{ item.productName }}</div>
                    <div class="text-muted small">
                      {{ formatPrice(item.price) }} × {{ item.quantity }}
                    </div>
                  </div>
                  <div class="d-flex align-items-center gap-2">
                    <span class="text-primary fw-bold">{{ formatPrice(calcItemTotal(item.price, item.quantity)) }}</span>
                    <button class="btn btn-sm btn-outline-danger py-0 px-1" @click="removeFromCart(item.productId)">×</button>
                  </div>
                </div>

                <div class="mt-3 pt-2 border-top">
                  <div class="d-flex justify-content-between fw-bold fs-6">
                    <span>合計</span>
                    <span class="text-primary">{{ formatPrice(cartTotal) }}</span>
                  </div>
                </div>

                <BFormGroup class="mt-3" label="備註（選填）" label-for="remark">
                  <BFormTextarea id="remark" v-model="remark" rows="2" placeholder="請輸入備註..." />
                </BFormGroup>

                <div class="d-grid mt-3">
                  <BButton variant="success" :disabled="checkoutLoading || cart.length === 0" @click="checkout">
                    {{ checkoutLoading ? '處理中...' : '立即結帳' }}
                  </BButton>
                </div>
              </div>
            </BCardBody>
          </BCard>
        </BCol>
      </BRow>
    </div>
  </MemberLayout>
</template>
