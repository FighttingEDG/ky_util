import { createRouter, createWebHistory } from 'vue-router'
import CardList from '../views/CardList.vue'
import CardReview from '../views/CardReview.vue'
import CardForm from '../views/CardForm.vue'
import MistakeList from '../views/MistakeList.vue'
import MistakeReview from '../views/MistakeReview.vue'
import MistakeForm from '../views/MistakeForm.vue'

const routes = [
  { path: '/', redirect: '/cards' },
  { path: '/cards', component: CardList },
  { path: '/review', component: CardReview },
  { path: '/cards/new', component: CardForm },
  { path: '/cards/edit/:id', component: CardForm },
  { path: '/mistakes', component: MistakeList },
  { path: '/mistakes/review', component: MistakeReview },
  { path: '/mistakes/new', component: MistakeForm },
  { path: '/mistakes/edit/:id', component: MistakeForm }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
