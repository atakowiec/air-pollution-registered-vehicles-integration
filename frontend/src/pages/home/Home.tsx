import {MainNavbar} from "../../components/MainNavbar.tsx";
import {title} from "../../util/title.ts";

export default function Home() {
  title("Home")

  return (
    <>
      <MainNavbar/>
    </>
  )
}