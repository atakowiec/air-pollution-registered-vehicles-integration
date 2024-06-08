import {ProgressStatus} from "../hooks/useProgress.ts";

export function formatDate(date?: number) {
  return date ? new Date(date).toLocaleString() : undefined
}

export function round(value: number) {
  return value > 1 ? Math.round(value * 1000) / 1000 : parseFloat(value.toPrecision(3))
}

export function formatDuration(duration: number) {
  if (duration <= 0) return "00:00:00"
  return new Date(duration).toISOString().slice(11, 19)
}

export function formatNumber(number: number) {
  if (isNaN(number)) return "-"

  return new Intl.NumberFormat().format(number)
}

export function isEmpty(value: any) {
  if (Array.isArray(value)) return value.length === 0

  if (typeof value === "object" && value !== null)
    return Object.keys(value).length == 0

  return false;
}

export function translateImportStatus(status: ProgressStatus) {
  switch (status) {
    case "NOT_STARTED":
      return "Nie rozpoczęto"
    case "IN_PROGRESS":
      return "W trakcie"
    case "FINISHED":
      return "Zakończono"
    case "FAILED":
      return "Niepowodzenie"
  }
}