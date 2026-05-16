<script>
import Layout from '@/layouts/vertical'
import { getAdminProducts, createProduct, updateProduct } from '@/api/product'
import { useAuthStore } from '@/state/pinia'

export default {
  name: 'ProductManage',
  components: { Layout },
  data() {
    return {
      products: [],
      loading: false,
      error: '',

      // Add Modal
      showAddModal: false,
      addForm: { productName: '', price: '', quantity: '', status: 1 },
      addLoading: false,
      addError: '',

      // Edit Modal
      showEditModal: false,
      editForm: { productId: '', productName: '', price: '', quantity: '', status: 1 },
      editLoading: false,
      editError: '',
    }
  },
  computed: {
    auth() { return useAuthStore() },
    username() { return this.auth.username },
  },
  async created() {
    await this.loadProducts()
  },
  methods: {
    async loadProducts() {
      this.loading = true
      this.error = ''
      try {
        const res = await getAdminProducts()
        if (res.success === 1) {
          this.products = res.data || []
        } else {
          this.error = res.errMsg || '載入商品失敗'
        }
      } catch (e) {
        this.error = e?.response?.data?.errMsg || '系統錯誤，請稍後再試'
      } finally {
        this.loading = false
      }
    },

    formatPrice(val) {
      return 'NT$ ' + Number(val).toLocaleString('zh-TW', { minimumFractionDigits: 2 })
    },

    openAddModal() {
      this.addForm = { productName: '', price: '', quantity: '', status: 1 }
      this.addError = ''
      this.showAddModal = true
    },

    async submitAdd() {
      this.addError = ''
      if (!this.addForm.productName || this.addForm.price === '' || this.addForm.quantity === '') {
        this.addError = '請填寫所有必填欄位'
        return
      }
      this.addLoading = true
      try {
        const res = await createProduct({
          productName: this.addForm.productName,
          price: Number(this.addForm.price),
          quantity: Number(this.addForm.quantity),
          status: Number(this.addForm.status),
        })
        if (res.success === 1) {
          this.showAddModal = false
          await this.loadProducts()
        } else {
          this.addError = res.errMsg || '新增失敗'
        }
      } catch (e) {
        this.addError = e?.response?.data?.errMsg || '系統錯誤，請稍後再試'
      } finally {
        this.addLoading = false
      }
    },

    openEditModal(product) {
      this.editForm = {
        productId: product.productId,
        productSn: product.productSn,
        productName: product.productName,
        price: product.price,
        quantity: product.quantity,
        status: product.status,
        version: product.version,
      }
      this.editError = ''
      this.showEditModal = true
    },

    async submitEdit() {
      this.editError = ''
      if (!this.editForm.productName || this.editForm.price === '' || this.editForm.quantity === '') {
        this.editError = '請填寫所有必填欄位'
        return
      }
      this.editLoading = true
      try {
        const res = await updateProduct(this.editForm.productId, {
          productName: this.editForm.productName,
          price: Number(this.editForm.price),
          quantity: Number(this.editForm.quantity),
          status: Number(this.editForm.status),
          version: this.editForm.version,
        })
        if (res.success === 1) {
          this.showEditModal = false
          await this.loadProducts()
        } else if (res.success === -1 && res.errMsg && res.errMsg.includes('樂觀')) {
          this.editError = '資料已被更新，請重新整理後再試'
        } else {
          this.editError = res.errMsg || '更新失敗'
        }
      } catch (e) {
        this.editError = e?.response?.data?.errMsg || '系統錯誤，請稍後再試'
      } finally {
        this.editLoading = false
      }
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
      <h4 class="mb-0">商品管理</h4>
      <BButton variant="primary" @click="openAddModal">
        <i class="bx bx-plus me-1"></i>新增商品
      </BButton>
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
                <th>商品ID</th>
                <th>商品名稱</th>
                <th>價格</th>
                <th>庫存</th>
                <th>狀態</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="product in products" :key="product.productId">
                <td>{{ product.productId }}</td>
                <td>{{ product.productName }}</td>
                <td>{{ formatPrice(product.price) }}</td>
                <td>{{ product.quantity }}</td>
                <td>
                  <BBadge :variant="product.status === 1 ? 'success' : 'secondary'">
                    {{ product.status === 1 ? '上架' : '下架' }}
                  </BBadge>
                </td>
                <td>
                  <BButton size="sm" variant="outline-primary" @click="openEditModal(product)">
                    <i class="bx bx-edit me-1"></i>編輯
                  </BButton>
                </td>
              </tr>
              <tr v-if="products.length === 0">
                <td colspan="6" class="text-center text-muted py-4">目前沒有商品資料</td>
              </tr>
            </tbody>
          </table>
        </div>
      </BCardBody>
    </BCard>

    <!-- Add Product Modal -->
    <BModal v-model="showAddModal" title="新增商品" hide-footer>
      <BAlert v-if="addError" variant="danger" :model-value="true">{{ addError }}</BAlert>
      <BForm @submit.prevent="submitAdd">
        <BFormGroup class="mb-3" label="商品名稱 *" label-for="add-name">
          <BFormInput id="add-name" v-model="addForm.productName" placeholder="請輸入商品名稱" required />
        </BFormGroup>
        <BFormGroup class="mb-3" label="價格 *" label-for="add-price">
          <BFormInput id="add-price" v-model="addForm.price" type="number" min="0" step="0.01" placeholder="請輸入價格" required />
        </BFormGroup>
        <BFormGroup class="mb-3" label="庫存數量 *" label-for="add-quantity">
          <BFormInput id="add-quantity" v-model.number="addForm.quantity" type="number" min="0" placeholder="請輸入庫存數量" required />
        </BFormGroup>
        <BFormGroup class="mb-3" label="狀態" label-for="add-status">
          <BFormSelect id="add-status" v-model="addForm.status">
            <option :value="1">上架</option>
            <option :value="0">下架</option>
          </BFormSelect>
        </BFormGroup>
        <div class="d-flex justify-content-end gap-2">
          <BButton variant="secondary" @click="showAddModal = false">取消</BButton>
          <BButton type="submit" variant="primary" :disabled="addLoading">
            {{ addLoading ? '新增中...' : '確認新增' }}
          </BButton>
        </div>
      </BForm>
    </BModal>

    <!-- Edit Product Modal -->
    <BModal v-model="showEditModal" title="編輯商品" hide-footer>
      <BAlert v-if="editError" variant="danger" :model-value="true">{{ editError }}</BAlert>
      <BForm @submit.prevent="submitEdit">
        <BFormGroup class="mb-3" label="商品名稱 *" label-for="edit-name">
          <BFormInput id="edit-name" v-model="editForm.productName" placeholder="請輸入商品名稱" required />
        </BFormGroup>
        <BFormGroup class="mb-3" label="價格 *" label-for="edit-price">
          <BFormInput id="edit-price" v-model="editForm.price" type="number" min="0" step="0.01" placeholder="請輸入價格" required />
        </BFormGroup>
        <BFormGroup class="mb-3" label="庫存數量 *" label-for="edit-quantity">
          <BFormInput id="edit-quantity" v-model.number="editForm.quantity" type="number" min="0" placeholder="請輸入庫存數量" required />
        </BFormGroup>
        <BFormGroup class="mb-3" label="狀態" label-for="edit-status">
          <BFormSelect id="edit-status" v-model="editForm.status">
            <option :value="1">上架</option>
            <option :value="0">下架</option>
          </BFormSelect>
        </BFormGroup>
        <div class="d-flex justify-content-end gap-2">
          <BButton variant="secondary" @click="showEditModal = false">取消</BButton>
          <BButton type="submit" variant="primary" :disabled="editLoading">
            {{ editLoading ? '更新中...' : '確認更新' }}
          </BButton>
        </div>
      </BForm>
    </BModal>
  </Layout>
</template>
