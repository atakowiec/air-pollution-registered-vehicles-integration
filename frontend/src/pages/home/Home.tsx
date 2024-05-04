import {MainNavbar} from "../../componentes/MainNavbar.tsx";
import {title} from "../../util/title.ts";

export default function Home() {
  title("Home")

  return (
    <>
      <MainNavbar/>
    </>
  )
}