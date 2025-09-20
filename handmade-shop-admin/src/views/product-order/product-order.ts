import { api } from '@/utils/api-instance'

export const productOrderQueryOptions = async (keyword: string, id: string) => {
  return (
    await api.productOrderForAdminController.query({ body: { query: { name: keyword, id } } })
  ).content
}
