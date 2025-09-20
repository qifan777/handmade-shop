import { api } from '@/utils/api-instance'

export const handicraftQueryOptions = async (keyword: string, id: string) => {
  return (await api.handicraftForAdminController.query({ body: { query: { name: keyword, id } } }))
    .content
}
